package pl.asie.computronics.oc;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import li.cil.oc.api.event.RobotAnalyzeEvent;
import li.cil.oc.api.event.RobotRenderEvent;
import li.cil.oc.api.internal.Agent;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.api.network.Node;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.reference.Mods;

import java.util.Locale;

/**
 * @author Vexatos
 */
public class ColorfulUpgradeHandler {

	@SubscribeEvent
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRobotAnalyze(RobotAnalyzeEvent e) {
		int color = getColor(e.agent);
		if(color < 0) {
			return;
		}
		e.player.addChatMessage(new ChatComponentTranslation("chat.computronics.colorful_upgrade.color", "0x"
			+ String.format("%06x", color).toUpperCase(Locale.ENGLISH)));
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRobotRender(RobotRenderEvent e) {
		int color = -1;
		if(e.agent instanceof Robot) {
			Robot robot = ((Robot) e.agent);
			for(int i = 0; i < robot.getSizeInventory(); ++i) {
				//Environment component = robot.getComponentInSlot(i);
				//if(component instanceof RobotUpgradeColorful) {
				//	color = ((RobotUpgradeColorful) component).getColor();
				//	break;
				//}
				ItemStack stack = robot.getStackInSlot(i);
				if(stack != null && stack.getItem() instanceof ItemOpenComputers && stack.getItemDamage() == 7) {
					NBTTagCompound tag = ((ItemOpenComputers) stack.getItem()).dataTag(stack);
					if(tag.hasKey("computronics:color")) {
						int newcol = tag.getInteger("computronics:color");
						if(newcol > color) {
							color = newcol;
						}
					}
				}
			}
		}
		if(color < 0) {
			return;
		}
		color = color & 0xFFFFFF;
		GL11.glColor3ub((byte) ((color >> 16) & 0xFF), (byte) ((color >> 8) & 0xFF), (byte) (color & 0xFF));
	}

	private int getColor(Agent agent) {
		try {
			for(Node node : agent.machine().node().reachableNodes()) {
				if(node != null && node.host() instanceof RobotUpgradeColorful) {
					return ((RobotUpgradeColorful) node.host()).getColor();
				}
			}
		} catch(NullPointerException e) {
			return -1;
		}
		return -1;
	}
}
