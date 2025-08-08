package net.frosty.fouriermachine.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.frosty.fouriermachine.FourierComputation;
import net.frosty.fouriermachine.InterpolatePoints;
import net.frosty.fouriermachine.SVGpoints;
import net.frosty.fouriermachine.entity.FourierArmManager;
import net.frosty.fouriermachine.entity.custom.FourierArm;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.ArrayList;

public class CreateArmCommand {
    private static ArrayList<FourierArmManager> managerList = new ArrayList<>();

     public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, CommandManager.RegistrationEnvironment environment) {
         dispatcher.register(CommandManager.literal("fourier")
                 .then(CommandManager.argument("item", StringArgumentType.word())
                 .then(CommandManager.argument("x", IntegerArgumentType.integer())
                 .then(CommandManager.argument("y", IntegerArgumentType.integer())
                 .then(CommandManager.argument("z", IntegerArgumentType.integer())
                 .then(CommandManager.argument("iCount", IntegerArgumentType.integer())
                 .then(CommandManager.argument("size", FloatArgumentType.floatArg())
                 .then(CommandManager.argument("speed", FloatArgumentType.floatArg()).executes(CreateArmCommand::run)))))))));

         dispatcher.register(CommandManager.literal("pi")
                 .then(CommandManager.argument("x", IntegerArgumentType.integer())
                 .then(CommandManager.argument("y", IntegerArgumentType.integer())
                 .then(CommandManager.argument("z", IntegerArgumentType.integer())
                 .then(CommandManager.argument("size", FloatArgumentType.floatArg())
                 .then(CommandManager.argument("speed", FloatArgumentType.floatArg()).executes(CreateArmCommand::runPi)))))));

         dispatcher.register(CommandManager.literal("fourierArmSize")
                 .then(CommandManager.argument("size", FloatArgumentType.floatArg())
                 .executes(CreateArmCommand::setArmSize)));

         dispatcher.register(CommandManager.literal("particleSize")
                 .then(CommandManager.argument("size", FloatArgumentType.floatArg())
                 .executes(CreateArmCommand::setParticleSize)));

         dispatcher.register(CommandManager.literal("resetFourier").executes(CreateArmCommand::reset));

         dispatcher.register(CommandManager.literal("freeze").executes(CreateArmCommand::freeze));
     }


    private static int freeze(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        FourierArmManager.isFrozen = !(FourierArmManager.isFrozen);
        return 1;
    }

     private static int reset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
         for (FourierArmManager m : managerList){
             m.resetArms();
         }
         FourierArm.resetEndpoints();
         return 1;
     }

     private static int setArmSize(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
         Float size = FloatArgumentType.getFloat(context, "size");
         FourierArm.setArmThickness(size);
         return 1;
     }

    private static int setParticleSize(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Float size = FloatArgumentType.getFloat(context, "size");
        FourierArm.setParticleSize(size);
        return 1;
    }



     private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
         FourierComputation computer = new FourierComputation();

         System.out.println("Command run");
//         int N = 36;
//
//         Vector2d[] points = new Vector2d[N];
//         for (int i=0; i<(N)/4;i++){
//             points[i] = new Vector2d((double) (-(N - 4) / 4 + 1) /2 + i, (double) (-(N - 4) / 4 + 1) /2); //bottom
//             points[i+(N/4)] = new Vector2d((double) (+(N - 4) / 4 + 1) /2, (double) (-(N - 4) / 4 + 1) /2 + i); //right
//             points[i+(N/4)*(2)] = new Vector2d((double) (+(N - 4) / 4 + 1) /2 - i, (double) (+(N - 4) / 4 + 1) /2); //top
//             points[i+(N/4)*(3)] = new Vector2d((double) (-(N - 4) / 4 + 1) /2, (double) (+(N - 4) / 4 + 1) /2 - i); //left
//         }
         String item = StringArgumentType.getString(context, "item").toString();
         Integer x = IntegerArgumentType.getInteger(context, "x");
         Integer y = IntegerArgumentType.getInteger(context, "y");
         Integer z = IntegerArgumentType.getInteger(context, "z");
         Integer iCount = IntegerArgumentType.getInteger(context, "iCount");
         Float size = FloatArgumentType.getFloat(context, "size");
         Float speed = FloatArgumentType.getFloat(context, "speed");

         Vector2d[] points;
         switch (item){
             case "sword":
                 System.out.println("generating sword...");
                 points = SVGpoints.getSword();
                 break;
             case "milo":
                 System.out.println("generating milo...");
                 points = SVGpoints.getMilo();
                 break;
             case "creeper":
                 System.out.println("generating creeper...");
                 points = SVGpoints.getCreeper();
                 break;
             case "minecraft":
                 System.out.println("generating minecraft...");
                 points = SVGpoints.getMinecraft();
                 break;
             case "dog":
                 System.out.println("generating dog...");
                 points = SVGpoints.getDog();
                 break;
             case "pi":
                 System.out.println("generating pi...");
                 points = SVGpoints.getPi();
                 break;
             case "einstein":
                 System.out.println("generating einstein...");
                 points = SVGpoints.getEinstein();
                 break;
             case "cat":
                 System.out.println("generating cat...");
                 points = SVGpoints.getCat();
                 break;
             case "stellaOctangula":
                 System.out.println("generating stellaOctangula...");
                 points = SVGpoints.getStellaOctangula();
                 break;
             default:
                 System.out.println("generating fallback...");
                 points = SVGpoints.getPi();
                 break;
         }

         points = InterpolatePoints.interpolate(points, iCount);
         System.out.println("Interpolating " + iCount + " times...");

         for (Vector2d point : points) {
             System.out.println(point.toString());
         }

         Double[][] Y = computer.DFT(points,size);

         System.out.println("computed DFT");

         MinecraftServer server = context.getSource().getServer();
         DustParticleEffect particle = new DustParticleEffect(new Vector3f(1,0,0),3.0F);

         FourierArmManager manager = new FourierArmManager();
         manager.register(server, particle);
         manager.defineArms(Y,x,y,z,server,speed);
         managerList.add(manager);

         System.out.println("Fourier Arms Created");
         return 1;
     }

    private static int runPi(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        Integer x = IntegerArgumentType.getInteger(context, "x");
        Integer y = IntegerArgumentType.getInteger(context, "y");
        Integer z = IntegerArgumentType.getInteger(context, "z");
        Float size = FloatArgumentType.getFloat(context, "size");
        Float speed = FloatArgumentType.getFloat(context, "speed");
        MinecraftServer server = context.getSource().getServer();
        DustParticleEffect particle = new DustParticleEffect(new Vector3f(1,0,0),3.0F);

        FourierArmManager manager = new FourierArmManager();
        manager.register(server, particle);
        manager.defineArms(x,y,z,server,size,speed);
        managerList.add(manager);

        System.out.println("Pi Visualisation Created}");
        return 1;
    }

}
