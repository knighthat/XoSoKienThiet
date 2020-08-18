package me.knighthat.XoSoKienThiet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class XoSoKienThiet extends JavaPlugin implements CommandExecutor {
	public Map<Player, Boolean> dangChoi = new HashMap<Player, Boolean>();
	public Map<Player, List<Integer>> conSo = new HashMap<Player, List<Integer>>();
	public Map<Player, Long> thamGia = new HashMap<Player, Long>();
	private List<Integer> soNgauNhien = new ArrayList<Integer>();

	CaiDat cd = new CaiDat(this);

	@Override
	public void onEnable() {
		getCommand("xoso").setExecutor(this);
		getServer().getPluginManager().registerEvents(new SuKien(this), this);
		henGio();
		khoiDong();
	}

	public String mau(String a) {
		return ChatColor.translateAlternateColorCodes('&', a);
	}

	private File tep = null;

	public void khoiDong() {
		if (tep == null)
			tep = new File(getDataFolder(), "cachChoi.txt");
		if (!tep.exists())
			saveResource("cachChoi.txt", false);
	}

	public void cachChoi(CommandSender sender) throws IOException {

		File file = new File(getDataFolder(), "cachChoi.txt");
		BufferedReader howto = null;
		try {
			howto = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF8")));
		} catch (FileNotFoundException e) {
			khoiDong();
		}
		String doc = null;

		while ((doc = howto.readLine()) != (null))
			sender.sendMessage(mau(doc));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 1 && (args[0].equalsIgnoreCase("tailai") || args[0].equalsIgnoreCase("datlai"))) {
			if (!sender.hasPermission("xoso.admin")) {
				sender.sendMessage(mau(cd.lay().getString("TienTo") + cd.lay().getString("KhongQuyen")));
			} else {
				if (args[0].equalsIgnoreCase("tailai")) {
					dangChoi.clear();
					conSo.clear();
					soNgauNhien.clear();
					thamGia.clear();
					cd.taiLai();
					sender.sendMessage(mau(cd.lay().getString("TienTo") + cd.lay().getString("TaiLai")));
				} else {
					dangChoi.clear();
					conSo.clear();
					soNgauNhien.clear();
					thamGia.clear();
					sender.sendMessage(mau(cd.lay().getString("TienTo") + cd.lay().getString("DatLai")));
				}
			}
			return true;
		}
		if (args.length == 1 && (args[0].equalsIgnoreCase("cachchoi"))) {
			try {
				cachChoi(sender);
			} catch (IOException e) {
				// sad
			}
			return true;
		}
		if (args.length == 1 && (args[0].equalsIgnoreCase("thamgia"))) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(mau(cd.lay().getString("TienTo") + "Chỉ người chơi mới được tham gia!"));
			} else {
				Player player = (Player) sender;
				if (!dangChoi.containsKey(player)) {
					if (player.hasPermission("xoso.thamgia")) {
						player.sendMessage(mau(cd.lay().getString("TienTo") + cd.lay().getString("ThamGia")
								.replace("{CAP}", "" + cd.lay().getInt("CaiDat.CapSo"))));
						player.sendMessage(
								mau("&6Bạn có " + cd.lay().getInt("CaiDat.ThoiGianCho") + " giây trước khi hết hạn!"));
						player.sendMessage(mau("&6Nhập \"&6&lhuy&6\" để không chơi nữa."));
						thamGia.put(player,
								(System.currentTimeMillis() + (1000 * cd.lay().getInt("CaiDat.ThoiGianCho"))));
					}
				} else {
					player.sendMessage(mau(cd.lay().getString("TienTo") + cd.lay().getString("DaThamgGia")));
					player.sendMessage(
							mau("&cBạn đã chọn: &f&l" + conSo.get(player)).replace("[", "").replace("]", ""));
				}
			}
			return true;
		}
		if (sender.hasPermission("xoso.admin")) {
			sender.sendMessage(mau("&9/xoso tailai: &fTải lại plugin Xổ Số Kiến Thiết."));
			sender.sendMessage(mau("&9/xoso datlai: &fĐặt lại từ đầu."));
		}
		sender.sendMessage(mau("&9/xoso cachchoi: &fXem cách chơi xổ số."));
		sender.sendMessage(mau("&9/xoso: &fHiển thị các câu lệnh."));
		return true;
	}

	public void doanSo() {
		Random ngaunhien = new Random();
		for (int i = 0; i < cd.lay().getInt("CaiDat.CapSo"); i++) {
			soNgauNhien.add(ngaunhien.nextInt(100));
		}
	}

	private int giaithuong;

	private String layGiai() {
		if (giaithuong == 1)
			return "GiaiNhat";
		if (giaithuong == 2)
			return "GiaiNhi";
		return "GiaiBa";
	}

	public void thongBao(Player player) {
		for (Player p : getServer().getOnlinePlayers())
			if (cd.lay().getBoolean("GiaiThuong.BatThongBao")) {
				p.sendMessage(mau(cd.lay().getString("TienTo") + cd.lay().getString("GiaiThuong.ThongBao")
						.replace("{GIAITHUONG}", cd.lay().getString(layGiai())).replace("{TEN}", player.getName())
						.replace("{BIETDANH}", player.getDisplayName())));
			}
	}

	public void henGio() {
		int hengio = cd.lay().getInt("HenGioXoSo");

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if (!dangChoi.isEmpty())
					for (Player player : getServer().getOnlinePlayers()) {
						if (conSo.containsKey(player)) {
							doanSo();
							player.sendMessage(mau(cd.lay().getString("TienTo"))
									+ mau("&eCon số may mắn cho lần này là: " + "&f&l" + soNgauNhien).replace("[", "")
											.replace("]", ""));
							if (soNgauNhien.equals(conSo.get(player))) {
								for (String i : cd.lay().getStringList("GiaiThuong.GiaiNhat")) {
									if (i.contains("TINNHAN:")) {
										player.sendMessage(mau(i).replace("TINNHAN:", ""));
									} else {
										getServer().dispatchCommand(getServer().getConsoleSender(),
												i.replace("{NGUOICHOI}", player.getName()));
									}
								}
								giaithuong = 1;
								thongBao(player);
							} else {
								int giai = 0;
								for (int i = 0; i < soNgauNhien.size(); i++) {
									if (conSo.get(player).get(i).equals(soNgauNhien.get(i))) {
										giai++;
									}
								}
								if (giai == (soNgauNhien.size() - 1)) {
									for (String i : cd.lay().getStringList("GiaiThuong.GiaiNhi")) {
										if (i.contains("TINNHAN:")) {
											player.sendMessage(mau(i).replace("TINNHAN:", ""));
										} else {
											getServer().dispatchCommand(getServer().getConsoleSender(),
													i.replace("{NGUOICHOI}", player.getName()));
										}
									}
									giaithuong = 2;
									thongBao(player);

								} else {
									if (giai == (soNgauNhien.size() - 2)) {
										for (String i : cd.lay().getStringList("GiaiThuong.GiaiBa")) {
											if (i.contains("TINNHAN:")) {
												player.sendMessage(mau(i).replace("TINNHAN:", ""));
											} else {
												getServer().dispatchCommand(getServer().getConsoleSender(),
														i.replace("{NGUOICHOI}", player.getName()));
											}
										}
										giaithuong = 3;
										thongBao(player);
									}
								}
							}
							dangChoi.remove(player);
							conSo.remove(player);
						}
					}
				soNgauNhien.clear();
			}
		}, hengio * 20, hengio * 20);
	}

}
