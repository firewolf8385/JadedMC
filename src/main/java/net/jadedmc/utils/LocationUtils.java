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

package net.jadedmc.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A collection of tools to help deal with locations.
 */
public class LocationUtils {

    /**
     * Get a location from a configuration section.
     * @param config ConfigurationSection for the location.
     * @return Location object stored.
     */
    public static Location fromConfig(ConfigurationSection config) {
        World world = Bukkit.getWorlds().get(0);

        double x,y,z;
        float yaw,pitch;

        if(config.isSet("World")) {
            String worldName = config.getString("World");

            if(worldName != null && Bukkit.getWorld(worldName) != null) {
                world = Bukkit.getWorld(worldName);
            }
        }

        // X Coordinate
        if(config.isSet("x")) {
            x = config.getDouble("x");
        }
        else {
            x = config.getDouble("X");
        }

        // Y Coordinate
        if(config.isSet("y")) {
            y = config.getDouble("y");
        }
        else {
            y = config.getDouble("Y");
        }

        // Z coordinate
        if(config.isSet("z")) {
            z = config.getDouble("z");
        }
        else {
            z = config.getDouble("Z");
        }

        // Yaw
        if(config.isSet("yaw")) {
            yaw = (float) config.getDouble("yaw");
        }
        else if(config.isSet("Yaw")){
            yaw = (float) config.getDouble("Yaw");
        }
        else {
            yaw = 0;
        }

        // Pitch
        if(config.isSet("pitch")) {
            pitch = (float) config.getDouble("pitch");
        }
        else if(config.isSet("Pitch")){
            pitch = (float) config.getDouble("Pitch");
        }
        else {
            pitch = 0;
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Get a location from a formatted string.
     * @param string Formatted string to get location in.
     * @return Location inside that string.
     */
    public static Location fromString(String string) {
        // Get the components of the location.
        String[] args = string.split(",");

        // Represents a location without yaw and pitch, usually used for blocks.
        if(args.length == 4) {
            World world = Bukkit.getWorld(args[0]);
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);

            return new Location(world, x, y, z);
        }

        // Represents a location with yaw and pitch, usually used for entities.
        if(args.length == 6) {
            World world = Bukkit.getWorld(args[0]);
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            float yaw = Float.parseFloat(args[4]);
            float pitch = Float.parseFloat(args[5]);

            return new Location(world, x, y, z, yaw, pitch);
        }

        // Location isn't formatted correctly.
        return null;
    }

    /**
     * Gets the equivalent location of a given location, in a new world.
     * @param world World to get new location in.
     * @param location Location to get equivalent of.
     * @return New location.
     */
    public static Location replaceWorld(World world, Location location) {
        return new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}