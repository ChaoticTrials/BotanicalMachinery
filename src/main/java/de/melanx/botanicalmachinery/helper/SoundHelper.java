package de.melanx.botanicalmachinery.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundEvent;

public class SoundHelper {

    public static void playSound(SoundEvent sound) {
        playSound(SimpleSound.master(sound, 1));
    }

    public static void playSound(ISound sound) {
        Minecraft.getInstance().getSoundHandler().play(sound);
    }
}
