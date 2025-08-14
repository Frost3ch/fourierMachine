package net.frosty.fouriermachine.entity;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.frosty.fouriermachine.entity.custom.FourierArm;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3i;

import java.util.ArrayList;

public class FourierArmManager {
    private  ArrayList<FourierArm> arms = new ArrayList<>();

    private boolean ready = false;
    private boolean resetScheduled = false;
    private  ArrayList<Vec3d> endpoints = new ArrayList<Vec3d>();
    public static boolean isFrozen = false;
    public static boolean isBlocks = false;

    public  ArrayList<Vec3d> getEndpoints(){
        return endpoints;
    }
    public void setEndpoints(ArrayList<Vec3d> newPoints){
        endpoints = newPoints;
    }

    public void resetArms(){
        endpoints.clear();
        resetScheduled = true;
        ready = false;
    }

    public void defineArms(Double[][] fourierY, Integer x, Integer y, Integer z, MinecraftServer server, Float speed) {
        FourierArm prev = null;
        FourierArm arm = null;
        for (int i = 0; i < fourierY.length; i++) {
            double freq = fourierY[i][2];
            double radius = fourierY[i][3] / 5;
            double phase = fourierY[i][4];

//            if (freq==1){
//                System.out.println("This is the og freq one.");
//            }

            if (prev == null) {
                arm = new FourierArm(new Vector3i((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)), new Vec3d(x, y, z), (float) radius*10, (float) (speed*(2* Math.PI*freq/ fourierY.length)), server.getOverworld(), (float) phase, (freq==1),this,0F);
            } else {
                arm = new FourierArm(new Vector3i((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)), prev, (float) radius*10, (float) (speed*(2* Math.PI*freq/fourierY.length)), server.getOverworld(), (float) phase, (freq==1), this,0F);
            }

            prev = arm;

            addArm(arm);

        }
        arm.setIsEnd(true);
        ready = true;
    }

    public void defineArms(Integer x, Integer y, Integer z, MinecraftServer server, Float size, Float speed) {
        FourierArm arm1 = new FourierArm(new Vector3i((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)), new Vec3d(x, y, z), (float) size, (float) (speed*1), server.getOverworld(), (float) 0, false,this,0F);
        FourierArm arm2 = new FourierArm(new Vector3i((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)), arm1, (float) size, (float) (speed * Math.PI), server.getOverworld(), (float) 0, false, this,0F);
        addArm(arm1);
        addArm(arm2);
        arm2.setIsEnd(true);
        ready = true;

        }

    public void defineArms(Integer x, Integer y, Integer z, MinecraftServer server, Float size, Float speed, Float translationSpeed, Integer approximity) {
        Integer approx = approximity;
        FourierArm prev = null;
        FourierArm arm = null;
        for (int i = 1; i < approx+1; i++) {
            if (i%2==1) {
                if (i==1) {
                    arm = new FourierArm(new Vector3i((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)), new Vec3d(x, y, z), (float) (size*4/Math.PI/i), (float) (speed*i), server.getOverworld(), (float) 0, true, this, translationSpeed);
                    addArm(arm);
                }
                else {
                    arm = new FourierArm(new Vector3i((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)), prev, (float) (size * 4 / Math.PI / i), (float) (speed*i), server.getOverworld(), (float) 0, false, this, translationSpeed);
                    addArm(arm);
                }
                prev = arm;
            }
        }

        arm.setIsEnd(true);
        ready = true;

    }

    //venus orbit demo
    public void defineArms(Integer x, Integer y, Integer z, MinecraftServer server, Float size, Float speed, boolean b) {
        FourierArm arm1 = new FourierArm(new Vector3i((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)), new Vec3d(x, y, z), (float) size, (float) (speed), server.getOverworld(), (float) 0, false,this, 0F,new Vec3i(0,255,0));
//        double ratio = (double) 8 /13;
//        double ratio = (double) 0.61518624;
        double ratio = (double) 1/13.4;
        double radius = Math.pow(ratio, (double) 2 /3);
        FourierArm arm2 = new FourierArm(new Vector3i((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)), arm1, (float) 0.05*size, (float) (speed/ratio), server.getOverworld(), (float)0, false, this, 0F);
        addArm(arm1);
        addArm(arm2);
        arm2.setIsEnd(true);
        ready = true;
    }


    public void addArm(FourierArm arm) {
        arms.add(arm);
    }

    public void register(MinecraftServer server) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (resetScheduled){
                arms.clear();
                resetScheduled = false;
            }
            if (!isFrozen) {
                if (ready) {
                    for (FourierArm arm : arms) {
                        arm.tick();
                    }
                }
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (ready) {
                for (FourierArm arm : arms) {
                    arm.render(context, server, isFrozen, isBlocks);
                }
            }
        });
    }
}
