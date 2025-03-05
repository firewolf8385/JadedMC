/*
 * This file is part of JadedMC, licensed under the MIT License.
 *
 * Copyright (c) JadedMC
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jadedmc.core.worlds;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.core.worlds.generators.JadedChunkGenerator;
import net.jadedmc.core.worlds.generators.VoidWorldGenerator;
import net.jadedmc.utils.FileUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages the creation, loading, and saving, or worlds through MongoDB.
 */
public class WorldManager {
    private final JadedMCPlugin plugin;
    private final Collection<JadedChunkGenerator> generators = new HashSet<>();

    /**
     * Creates the world manager.
     * @param plugin Instance of the plugin.
     */
    public WorldManager(final JadedMCPlugin plugin) {
        this.plugin = plugin;

        // Adds default generators.
        addGenerator(new VoidWorldGenerator());
    }

    /**
     * Adds a chunk generator to the plugin.
     * @param generator JadedChunkGenerator to add.
     */
    public void addGenerator(JadedChunkGenerator generator) {
        generators.add(generator);
    }

    /**
     * Loads a copy of a world from MongoDB given a world name.
     * @param worldName Name of the world to load.
     * @param copyName Name of the copy to make.
     * @return Copied world.
     */
    public CompletableFuture<World> copyWorld(String worldName, String copyName) {
        return copyWorld(worldName, new GridFSDownloadOptions(), copyName);
    }

    /**
     * Loads a copy of a world from MongoDB with a given world name and revision number.
     * @param worldName Name of the world to load.
     * @param revision Revision number of the world.
     * @param copyName Name of the copy to make.
     * @return Copied world.
     */
    public CompletableFuture<World> copyWorld(String worldName, int revision, String copyName) {
        return copyWorld(worldName, new GridFSDownloadOptions().revision(revision), copyName);
    }

    /**
     * Loads a copy of a world from MongoDB, given a world name and download options.
     * @param worldName Name of the world.
     * @param downloadOptions MongoDB download options.
     * @param copyName Name of the copy to make.
     * @return Copied world.
     */
    public CompletableFuture<World> copyWorld(String worldName, GridFSDownloadOptions downloadOptions, String copyName) {
        // Downloads the world from MongoDB.
        CompletableFuture<File> worldDownload = CompletableFuture.supplyAsync(() -> {
            // Get MongoDB connection.
            MongoDatabase database = plugin.getMongoDB().database();
            GridFSBucket gridFSBucket = GridFSBuckets.create(database, "storage");

            // Downloads a file to an output stream
            try (FileOutputStream streamToDownloadTo = new FileOutputStream(plugin.getServer().getWorldContainer() + "/" + worldName + ".zip")) {
                gridFSBucket.downloadToStream(worldName + ".zip", streamToDownloadTo, downloadOptions);
                streamToDownloadTo.flush();
            }
            catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            // Extracts the world from the zip file, and deletes the old zip file.
            File zipFile = new File(plugin.getServer().getWorldContainer() + "/" + worldName + ".zip");
            File worldFolder = new File(plugin.getServer().getWorldContainer() + "/" + copyName);
            ZipUtil.unpack(zipFile, worldFolder);
            zipFile.delete();

            // Deletes the uid.dat file if present. This allows multiple copies of a world to be loaded at the same time.
            File uidDat = new File(worldFolder, "uid.dat");
            if(uidDat.exists()) {
                uidDat.delete();
            }

            return worldFolder;
        });

        // Loads the world.
        return worldDownload.thenCompose(file -> CompletableFuture.supplyAsync(() -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                WorldCreator worldCreator = new WorldCreator(copyName);
                // TODO: Store proper generator to use in the file metadata.
                worldCreator.generator(new VoidWorldGenerator());
                Bukkit.createWorld(worldCreator);
            });

            // Wait for the world to be loaded.
            boolean loaded = false;
            World world = null;
            while(!loaded) {
                try {
                    Thread.sleep(60);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for(World w : Bukkit.getWorlds()) {
                    if(w.getName().equals(copyName)) {
                        loaded = true;
                        world = w;
                        break;
                    }
                }
            }

            // Disable autosave.
            world.setAutoSave(false);

            // Returns the resulting world.
            return world;
        }));
    }

    /**
     * Downloads a world from MongoDB given a world name.
     * @param worldName Name of the world to download.
     */
    public File downloadWorldSync(String worldName) {
        return downloadWorldSync(worldName, new GridFSDownloadOptions());
    }

    /**
     * Downloads a world from MongoDB given a world name and revision number.
     * @param worldName Name of the world to download.
     * @param revision Revision number of the world.
     */
    public File downloadWorldSync(String worldName, int revision) {
        return downloadWorldSync(worldName, new GridFSDownloadOptions().revision(revision));
    }

    /**
     * Downloads a world from MongoDB given a world name and configured download options.
     * @param worldName Name of the world to download.
     * @param downloadOptions MongoDB download options.
     */
    public File downloadWorldSync(String worldName, GridFSDownloadOptions downloadOptions) {
        // Get MongoDB connection.
        MongoDatabase database = plugin.getMongoDB().database();
        GridFSBucket gridFSBucket = GridFSBuckets.create(database, "storage");

        // Downloads a file to an output stream
        try (FileOutputStream streamToDownloadTo = new FileOutputStream(plugin.getServer().getWorldContainer() + "/" + worldName + ".zip")) {
            gridFSBucket.downloadToStream(worldName + ".zip", streamToDownloadTo, downloadOptions);
            streamToDownloadTo.flush();
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        // Extracts the world from the zip file, and deletes the old zip file.
        File zipFile = new File(plugin.getServer().getWorldContainer() + "/" + worldName + ".zip");
        File worldFolder = new File(plugin.getServer().getWorldContainer() + "/" + worldName);
        ZipUtil.unpack(zipFile, worldFolder);
        zipFile.delete();

        return worldFolder;
    }

    /**
     * Gets a generator from its id.
     * Returns a new void generator if not found.
     * @param id id of the generator.
     * @return Generator corresponding to that id.
     */
    public JadedChunkGenerator getGenerator(String id) {
        for(JadedChunkGenerator generator : generators) {
            if(generator.getId().equalsIgnoreCase(id)) {
                return generator;
            }
        }

        return new VoidWorldGenerator();
    }

    /**
     * Gets all currently loaded generators.
     * @return Collection of JadedChunkGenerators.
     */
    public Collection<JadedChunkGenerator> getGenerators() {
        return generators;
    }

    /**
     * Loads a world from MongoDB given a world name.
     * @param worldName Name of the world to load.
     * @return Loaded world.
     */
    public CompletableFuture<World> loadWorld(String worldName) {
        return loadWorld(worldName, new GridFSDownloadOptions());
    }

    /**
     * Loads a world from MongoDB given a world name and revision number.
     * @param worldName Name of the world to load.
     * @param revision Revision number of the world.
     * @return Loaded world.
     */
    public CompletableFuture<World> loadWorld(String worldName, int revision) {
        return loadWorld(worldName, new GridFSDownloadOptions().revision(revision));
    }

    /**
     * Loads a world from MongoDB, given a world name and download options.
     * @param worldName Name of the world.
     * @param downloadOptions MongoDB download options.
     * @return Loaded world.
     */
    public CompletableFuture<World> loadWorld(String worldName, GridFSDownloadOptions downloadOptions) {
        // Downloads the world from MongoDB.
        CompletableFuture<File> worldDownload = CompletableFuture.supplyAsync(() -> {
            // Get MongoDB connection.
            MongoDatabase database = plugin.getMongoDB().database();
            GridFSBucket gridFSBucket = GridFSBuckets.create(database, "storage");

            // Downloads a file to an output stream
            try (FileOutputStream streamToDownloadTo = new FileOutputStream(plugin.getServer().getWorldContainer() + "/" + worldName + ".zip")) {
                gridFSBucket.downloadToStream(worldName + ".zip", streamToDownloadTo, downloadOptions);
                streamToDownloadTo.flush();
            }
            catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            // Extracts the world from the zip file, and deletes the old zip file.
            File zipFile = new File(plugin.getServer().getWorldContainer() + "/" + worldName + ".zip");
            File worldFolder = new File(plugin.getServer().getWorldContainer() + "/" + worldName);
            ZipUtil.unpack(zipFile, worldFolder);
            zipFile.delete();

            // Deletes the uid.dat file if present. This allows multiple copies of a world to be loaded at the same time.
            File uidDat = new File(worldFolder, "uid.dat");
            if(uidDat.exists()) {
                uidDat.delete();
            }

            return worldFolder;
        });

        // Loads the world.
        return worldDownload.thenCompose(file -> CompletableFuture.supplyAsync(() -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                WorldCreator worldCreator = new WorldCreator(worldName);
                // TODO: Store proper generator to use in the file metadata.
                worldCreator.generator(new VoidWorldGenerator());
                Bukkit.createWorld(worldCreator);
            });

            // Wait for the world to be loaded.
            boolean loaded = false;
            World world = null;
            while(!loaded) {
                try {
                    Thread.sleep(60);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for(World w : Bukkit.getWorlds()) {
                    if(w.getName().equals(worldName)) {
                        loaded = true;
                        world = w;
                        break;
                    }
                }
            }

            // Returns the resulting world.
            return world;
        }));
    }

    /**
     * Saves a world folder to MongoDB.
     * <strong>Warning:</strong> Does not unload the world. Do that before using.
     * This is done to allow arena editors to work.
     * @param worldFolder Folder to save
     * @param name Name to save the world as.
     */
    public void saveWorld(File worldFolder, String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            File zipFile = new File(worldFolder.getParent(), name + ".zip");
            ZipUtil.pack(worldFolder, zipFile);

            MongoDatabase database = plugin.getMongoDB().database();
            GridFSBucket gridFSBucket = GridFSBuckets.create(database, "storage");


            try (InputStream streamToUploadFrom = new FileInputStream(zipFile) ) {
                // Defines options that specify configuration information for files uploaded to the bucket
                GridFSUploadOptions options = new GridFSUploadOptions()
                        .chunkSizeBytes(1048576)
                        .metadata(new Document("type", "zip archive"));
                // Uploads a file from an input stream to the GridFS bucket
                ObjectId fileId = gridFSBucket.uploadFromStream(name + ".zip", streamToUploadFrom, options);
                // Prints the "_id" value of the uploaded file
                System.out.println("The file id of the uploaded file is: " + fileId.toHexString());
            }
            catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            FileUtils.deleteDirectory(worldFolder);
            zipFile.delete();
        });
    }
}