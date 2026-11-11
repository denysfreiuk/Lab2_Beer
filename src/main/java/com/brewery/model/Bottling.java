package com.brewery.model;

import java.util.Objects;

/** The way the beer is bottled: volume (litres) and material of the container. */
public class Bottling {

    private double volume;
    private ContainerMaterial material;

    public Bottling() {
    }

    public Bottling(double volume, ContainerMaterial material) {
        this.volume = volume;
        this.material = material;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public ContainerMaterial getMaterial() {
        return material;
    }

    public void setMaterial(ContainerMaterial material) {
        this.material = material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bottling other)) {
            return false;
        }
        return Double.compare(volume, other.volume) == 0 && material == other.material;
    }

    @Override
    public int hashCode() {
        return Objects.hash(volume, material);
    }

    @Override
    public String toString() {
        return volume + " l, " + material;
    }
}
