package me.knighthat.XoSoKienThiet;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SuKien implements Listener {

	XoSoKienThiet plugin;

	public SuKien(XoSoKienThiet plugin) {
		this.plugin = plugin;
	}

	public BlockFace getPlayerDirection(Player player) {
		BlockFace dir = null;
		float y = player.getLocation().getYaw();
		if (y < 0) {
			y += 360;
		}
		y %= 360;
		int i = (int) ((y + 8) / 22.5);
		if (i == 0) {
			dir = BlockFace.SOUTH;
		} else if (i == 1) {
			dir = BlockFace.SOUTH_SOUTH_WEST;
		} else if (i == 2) {
			dir = BlockFace.SOUTH_WEST;
		} else if (i == 3) {
			dir = BlockFace.WEST_SOUTH_WEST;
		} else if (i == 4) {
			dir = BlockFace.WEST;
		} else if (i == 5) {
			dir = BlockFace.WEST_NORTH_WEST;
		} else if (i == 6) {
			dir = BlockFace.NORTH_WEST;
		} else if (i == 7) {
			dir = BlockFace.NORTH_NORTH_WEST;
		} else if (i == 8) {
			dir = BlockFace.NORTH;
		} else if (i == 9) {
			dir = BlockFace.NORTH_NORTH_EAST;
		} else if (i == 10) {
			dir = BlockFace.NORTH_EAST;
		} else if (i == 11) {
			dir = BlockFace.EAST_NORTH_EAST;
		} else if (i == 12) {
			dir = BlockFace.EAST;
		} else if (i == 13) {
			dir = BlockFace.EAST_SOUTH_EAST;
		} else if (i == 14) {
			dir = BlockFace.SOUTH_EAST;
		} else
			dir = BlockFace.SOUTH_SOUTH_EAST;
		return dir;
	}

	@EventHandler
	public void datBang(SignChangeEvent e) {

		if (e.getPlayer().hasPermission("xoso.admin")) {
			org.bukkit.material.Sign matSign;
			if (e.getLine(0).equalsIgnoreCase("[xoso]")) {
				if (e.getBlock().getType().equals(Material.SIGN_POST)) {
					e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.AIR);
					e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.SIGN_POST);
					matSign = new org.bukkit.material.Sign(Material.SIGN_POST);
				} else {
					e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.AIR);
					e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.WALL_SIGN);
					matSign = new org.bukkit.material.Sign(Material.WALL_SIGN);
				}
				Sign s = (Sign) e.getPlayer().getWorld().getBlockAt(e.getBlock().getLocation()).getState();
				matSign.setFacingDirection(getPlayerDirection(e.getPlayer()).getOppositeFace());
				s.setData(matSign);
				for (int i = 0; i < 4; i++) {
					s.setLine(i, plugin.mau(plugin.cd.lay().getString("Bang." + (i + 1))));
				}
				s.getLocation().setDirection(e.getPlayer().getLocation().getDirection());
				s.update();
				Player player = e.getPlayer();
				player.sendMessage(
						plugin.mau(plugin.cd.lay().getString("TienTo") + plugin.cd.lay().getString("DatBang")));
			}
		}
	}

	@EventHandler
	public void thamGia(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		BlockState tuongtac = e.getClickedBlock().getState();
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (tuongtac instanceof Sign) {
				Sign bang = (Sign) tuongtac;
				if (player.hasPermission("xoso.thamgia")) {
					if (!plugin.dangChoi.containsKey(player)) {
						if (!player.hasPermission("xoso.thamgia")) {
							player.sendMessage(plugin.mau(
									plugin.cd.lay().getString("TienTo") + plugin.cd.lay().getString("KhongQuyen")));
						} else {
							if (bang.getLine(0).equalsIgnoreCase(plugin.mau(plugin.cd.lay().getString("Bang.1")))) {
								player.sendMessage(plugin
										.mau(plugin.cd.lay().getString("TienTo") + plugin.cd.lay().getString("ThamGia")
												.replace("{CAP}", "" + plugin.cd.lay().getInt("CaiDat.CapSo"))));
								player.sendMessage(plugin.mau("&6Bạn có " + plugin.cd.lay().getInt("CaiDat.ThoiGianCho")
										+ " giây trước khi hết hạn!"));
								player.sendMessage(plugin.mau("&6Nhập \"&6&lhuy&6\" để không chơi nữa."));
								plugin.thamGia.put(player, (System.currentTimeMillis()
										+ (1000 * plugin.cd.lay().getInt("CaiDat.ThoiGianCho"))));
							}
						}
					} else {
						player.sendMessage(plugin
								.mau(plugin.cd.lay().getString("TienTo") + plugin.cd.lay().getString("DaThamGia")));
						player.sendMessage(plugin.mau("&cBạn đã chọn: &f&l" + plugin.conSo.get(player)).replace("[", "")
								.replace("]", ""));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void nhapSo(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (plugin.thamGia.containsKey(player)) {
			if (plugin.thamGia.get(player) > System.currentTimeMillis()) {
				e.setCancelled(true);
				String[] chat = e.getMessage().split(" ");
				List<Integer> so = new ArrayList<Integer>();
				if (chat[0].equalsIgnoreCase("huy")) {
					player.sendMessage(plugin.mau(plugin.cd.lay().getString("TienTo") + "&cĐã ngưng tham gia xổ số"));
					return;
				}
				if (chat.length != plugin.cd.lay().getInt("CaiDat.CapSo")) {
					player.sendMessage(plugin.mau(plugin.cd.lay().getString("TienTo") + "Cần nhập "
							+ plugin.cd.lay().getInt("CaiDat.CapSo") + "cặp số!"));
				}
				for (int i = 0; i < chat.length; i++) {
					if (chat[i].length() != 2) {
						player.sendMessage(plugin
								.mau(plugin.cd.lay().getString("TienTo") + chat[i] + "&c không phải cặp số (vd: 00)"));
						return;
					}
					if (!chat[i].matches("-?\\d+")) {
						player.sendMessage(
								plugin.mau(plugin.cd.lay().getString("TienTo") + chat[i] + "&c không phải là số!"));
						return;
					}
					so.add(Integer.parseInt(chat[i]));
				}
				plugin.conSo.put(player, so);
				player.sendMessage(plugin.mau(plugin.cd.lay().getString("TienTo") + "&aCon số may mắn của bạn là: &f&l"
						+ e.getMessage().replace(" ", ", ")));
				plugin.thamGia.remove(player);
				plugin.dangChoi.put(player, true);
			}
		}
	}
}
