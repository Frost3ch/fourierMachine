package net.frosty.fouriermachine.entity.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.frosty.fouriermachine.FourierMachine;
import net.frosty.fouriermachine.entity.FourierArmManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.OptionalDouble;


public class FourierArm {

    private float startingAngle;
    private Vector3i color;
    private Vec3d pivot;
    private float radius;
    private float rotationSpeed;
    private float currentAngle;
    private float previousAngle;
    private Vec3d endpoint;
    private FourierArm parentArm;
    private boolean isEnd = false;
    private boolean isPrimed = false;
    private FourierArmManager manager;
    private static float dSize;
    private static float dThickness;
    private float size;
    private float thickness;
    private float translationSpeed=0;
    private static Vec3i endColor;
    private static Vec3i altColor;

    private static boolean completedCycle = false;
    private boolean isF1 = false;

    private ArrayList<Vec3d> ends;

    public FourierArm(Vector3i color, Vec3d pivot, float radius, float rotationSpeed, World world, float angle, boolean f1, FourierArmManager m, Float tSpeed, Vec3i aColor) {
        startingAngle = angle;
        currentAngle = angle;
        this.color = color;
        this.pivot = pivot;
        this.radius = radius;
        this.rotationSpeed = rotationSpeed;
        double dx = radius * Math.cos(currentAngle);
        double dy = radius * Math.sin(currentAngle);
        endpoint = new Vec3d((pivot.x + dx), (pivot.y + dy), 0);
        isF1 = f1;
        manager=m;
        size = dSize;
        thickness = dThickness;
        translationSpeed = tSpeed;
        altColor = aColor;
        ends = new ArrayList<>();

        System.out.println("Fourier Arm Initialized");

    }

    public static void resetEndpoints(){
        completedCycle = false;
    }

    public static void setColor(Integer r, Integer g, Integer b) {
        endColor = new Vec3i(r,g,b);
    }
    public static void setParticleSize(Float s){
        dSize = 0.05F*s;
    }
    public static void setArmThickness(Float s){
        dThickness = 0.1F*s;
    }

    public FourierArm(Vector3i color, Vec3d pivot, float radius, float rotationSpeed, World world, float angle, boolean f1, FourierArmManager m, Float tSpeed) {
        startingAngle = angle;
        currentAngle = angle;
        this.color = color;
        this.pivot = pivot;
        this.radius = radius;
        this.rotationSpeed = rotationSpeed;
        double dx = radius * Math.cos(currentAngle);
        double dy = radius * Math.sin(currentAngle);
        endpoint = new Vec3d((pivot.x + dx), (pivot.y + dy), 0);
        isF1 = f1;
        manager=m;
        size = dSize;
        thickness = dThickness;
        translationSpeed = tSpeed;

        System.out.println("Fourier Arm Initialized");

    }

    public FourierArm(Vector3i color, FourierArm parent, float radius, float rotationSpeed, World world, float angle, boolean f1,FourierArmManager m, Float tSpeed) {
        startingAngle = angle;
        currentAngle = angle;
        this.color = color;
        this.pivot = parent.getEndpoint();
        this.parentArm = parent;
        this.radius = radius;
        this.rotationSpeed = rotationSpeed;
        double dx = radius * Math.cos(currentAngle);
        double dy = radius * Math.sin(currentAngle);
        endpoint = new Vec3d((pivot.x + dx), (pivot.y + dy), 0);
        isF1 = f1;
        manager=m;
        size = dSize;
        thickness = dThickness;
        translationSpeed = tSpeed;

        System.out.println("Fourier Arm Child Initialized");

    }

    public Vec3d getEndpoint(){
        return endpoint;
    }

    public void tick() {

        isPrimed = true;
        previousAngle = currentAngle;
        currentAngle += rotationSpeed;

        if (isF1) {
            if (translationSpeed!=0) {
                ArrayList<Vec3d> ep = manager.getEndpoints();
                ArrayList<Vec3d> temp = new ArrayList<>();
                int endpointCap = 2000;
                while (ep.size()>endpointCap){
                    ep.removeFirst();
                }
                for (Vec3d p:ep){
                        temp.add(new Vec3d(p.x + translationSpeed / 20, p.y, p.z));
                }
                System.out.println(temp.size());
                manager.setEndpoints(temp);
            }

            else{
                if (currentAngle - 4 * Math.PI > startingAngle) {
                    completedCycle = true;
                }
            }

        }
    }

    public void setIsEnd(boolean gamer){
        isEnd = gamer;
    }

    public void render(WorldRenderContext context, MinecraftServer server, boolean isFrozen, boolean isBlocks) {

        if (parentArm!=null) {
            pivot = parentArm.getEndpoint();
        }
        if (pivot==null){
            return;
        }
        float partialTicks = context.tickCounter().getTickDelta(false);
        float interpolatedAngle = previousAngle + (currentAngle - previousAngle) * partialTicks;

        if (isFrozen){
            interpolatedAngle = currentAngle;
        }

        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();

        double dx = radius * Math.cos(interpolatedAngle);
        double dy = radius * Math.sin(interpolatedAngle);
        endpoint = new Vec3d((pivot.x + dx), (pivot.y + dy), (pivot.z));


        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest();
        RenderSystem.lineWidth(5.0F);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance()
                .getBufferBuilders()
                .getEntityVertexConsumers();

        VertexConsumer consumer = immediate.getBuffer(RenderLayer.getDebugQuads());

        Matrix4f posMat = matrices.peek().getPositionMatrix();

        float normGrad = (float) (-1/((endpoint.y-pivot.y)/(endpoint.x-pivot.x)));
        float ffX = (float) ((thickness/2)*Math.cos(interpolatedAngle-Math.PI/2));
        float ffY = (float) ((thickness/2)*Math.sin(interpolatedAngle-Math.PI/2));

        // First vertex at pivot
        consumer.vertex(posMat, (float)pivot.x+ffX, (float)pivot.y+ffY, (float)pivot.z).color(color.x, color.y, color.z, 255);

        consumer.vertex(posMat, (float)pivot.x-ffX, (float)pivot.y-ffY, (float)pivot.z).color(color.x, color.y, color.z, 255);

        // Second vertex at endpoint
        consumer.vertex(posMat, (float)endpoint.x+ffX, (float)endpoint.y+ffY, (float)endpoint.z).color(color.x, color.y, color.z, 255);

        consumer.vertex(posMat, (float)endpoint.x-ffX, (float)endpoint.y-ffY, (float)endpoint.z).color(color.x, color.y, color.z, 255);


        immediate.draw(); // flushes the lines

        if (altColor!=null){
            if (ends!=null) {
                ends.add(endpoint);
                for (Vec3d ep:ends) {
                    immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                    consumer = immediate.getBuffer(RenderLayer.getDebugQuads());
                    posMat = matrices.peek().getPositionMatrix();

                    consumer.vertex(posMat, (float) ep.x + size, (float) ep.y + size, (float) ep.z).color(altColor.getX(), altColor.getY(), altColor.getZ(), 255);
                    consumer.vertex(posMat, (float) ep.x + size, (float) ep.y - size, (float) ep.z).color(altColor.getX(), altColor.getY(), altColor.getZ(), 255);
                    consumer.vertex(posMat, (float) ep.x - size, (float) ep.y + size, (float) ep.z).color(altColor.getX(), altColor.getY(), altColor.getZ(), 255);
                    consumer.vertex(posMat, (float) ep.x - size, (float) ep.y - size, (float) ep.z).color(altColor.getX(), altColor.getY(), endColor.getZ(), 255);

                    immediate.draw();
                }
            }
        }

        if (isEnd && isPrimed) {
            if (!completedCycle){
//                System.out.println("added another Endpoint...");
                ArrayList<Vec3d> endpoints = manager.getEndpoints();
                if (translationSpeed!=0){
                    FourierArm cArm = this;
                    while (cArm.parentArm!=null){
                        cArm = cArm.parentArm;
                    }
                    endpoints.add(new Vec3d((float) cArm.pivot.x,endpoint.y,endpoint.z));
                }
                else {
                    endpoints.add(endpoint);
                }

                manager.setEndpoints(endpoints);
            }
            if (isBlocks){
                server.getOverworld().setBlockState(new BlockPos((int)Math.round(endpoint.x),(int)Math.round(endpoint.y),(int)Math.round(endpoint.z)), Blocks.BLACK_CONCRETE.getDefaultState());
            }

            ArrayList<Vec3d> endpoints = manager.getEndpoints();
            if (endpoints!=null) {
                for (Vec3d endpoint : endpoints) {
                    immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                    consumer = immediate.getBuffer(RenderLayer.getDebugQuads());
                    posMat = matrices.peek().getPositionMatrix();

                    consumer.vertex(posMat, (float) endpoint.x + size, (float) endpoint.y + size, (float) endpoint.z).color(endColor.getX(), endColor.getY(), endColor.getZ(), 255);
                    consumer.vertex(posMat, (float) endpoint.x + size, (float) endpoint.y - size, (float) endpoint.z).color(endColor.getX(), endColor.getY(), endColor.getZ(), 255);
                    consumer.vertex(posMat, (float) endpoint.x - size, (float) endpoint.y + size, (float) endpoint.z).color(endColor.getX(), endColor.getY(), endColor.getZ(), 255);
                    consumer.vertex(posMat, (float) endpoint.x - size, (float) endpoint.y - size, (float) endpoint.z).color(endColor.getX(), endColor.getY(), endColor.getZ(), 255);

                    immediate.draw(); // flushes the lines

//            System.out.println("make particle");
//            server.getOverworld().spawnParticles(armParticle, endpoint.getX(), endpoint.getY(), endpoint.getZ(), 1, 0, 0, 0, 0);
                }
            }
        }

        RenderSystem.enableDepthTest();
        matrices.pop();



//        int noParticles = 20;
//        for (int i = 0; i < noParticles; i++) {
//            double mult = (double) i / (noParticles - 1);
//            double x = pivot.x + mult * dx;
//            double y = pivot.y + mult * dy;
//            double z = pivot.z;
//            serverWorld.spawnParticles(armParticle, x, y, z, 1, 0, 0,0,0);
//        }
    }

}
