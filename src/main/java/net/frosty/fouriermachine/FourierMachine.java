package net.frosty.fouriermachine;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.frosty.fouriermachine.command.CreateArmCommand;
import net.frosty.fouriermachine.entity.FourierArmManager;
import net.frosty.fouriermachine.entity.custom.FourierArm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FourierMachine implements ModInitializer {
	public static final String MOD_ID = "fouriermachine";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		FourierArm.setColor(0,0,0);
		FourierArm.setArmThickness(1F);
		FourierArm.setParticleSize(1F);
		CommandRegistrationCallback.EVENT.register(CreateArmCommand::register);

//		ServerLifecycleEvents.SERVER_STARTED.register(server-> {
//			FourierArmManager.register();
//			FourierArm arm = new FourierArm(new Vector3i(255,0,0),new Vec3d(0,100,0),10F,(float)Math.PI/4/20,server.getOverworld(),0F);
//			FourierArm arm2 = new FourierArm(new Vector3i(0,255,0),arm,5F,(float)Math.PI/2/20,server.getOverworld(),0F);
//			FourierArm arm3 = new FourierArm(new Vector3i(0,0,255),arm2, 2.5F,(float)Math.PI/1/20,server.getOverworld(),0F);
//			FourierArmManager.addArm(arm);
//			FourierArmManager.addArm(arm2);
//			FourierArmManager.addArm(arm3);
//
//			System.out.println("Fourier Arms Created");
//		});



	}

}