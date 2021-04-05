/*
 * This file is part of ViaBackwards - https://github.com/ViaVersion/ViaBackwards
 * Copyright (C) 2016-2021 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.matsv.viabackwards.protocol.protocol1_10to1_11.storage;

import nl.matsv.viabackwards.api.entities.storage.EntityStorage;

public class ChestedHorseStorage implements EntityStorage {
    private boolean chested;
    private int liamaStrength;
    private int liamaCarpetColor = -1;
    private int liamaVariant;

    public boolean isChested() {
        return chested;
    }

    public void setChested(boolean chested) {
        this.chested = chested;
    }

    public int getLiamaStrength() {
        return liamaStrength;
    }

    public void setLiamaStrength(int liamaStrength) {
        this.liamaStrength = liamaStrength;
    }

    public int getLiamaCarpetColor() {
        return liamaCarpetColor;
    }

    public void setLiamaCarpetColor(int liamaCarpetColor) {
        this.liamaCarpetColor = liamaCarpetColor;
    }

    public int getLiamaVariant() {
        return liamaVariant;
    }

    public void setLiamaVariant(int liamaVariant) {
        this.liamaVariant = liamaVariant;
    }

    @Override
    public String toString() {
        return "ChestedHorseStorage{" + "chested=" + chested + ", liamaStrength=" + liamaStrength + ", liamaCarpetColor=" + liamaCarpetColor + ", liamaVariant=" + liamaVariant + '}';
    }
}