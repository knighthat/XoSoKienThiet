package me.knighthat.XoSoKienThiet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CaiDat {

	XoSoKienThiet plugin;

	public CaiDat(XoSoKienThiet plugin) {
		this.plugin = plugin;
		khoiDong();
	}

	private File tep = null;
	private FileConfiguration config = null;
	private FileInputStream file;

	public void khoiDong() {
		if (tep == null)
			tep = new File(plugin.getDataFolder(), "caidat.yml");
		if (!tep.exists())
			plugin.saveResource("caidat.yml", false);
	}

	public void taiLai() {
		if (tep == null)
			tep = new File(plugin.getDataFolder(), "caidat.yml");

		config = YamlConfiguration.loadConfiguration(tep);

		InputStream dulieu = plugin.getResource("caidat.yml");
		if (dulieu != null) {
			YamlConfiguration macdinh = YamlConfiguration
					.loadConfiguration(new InputStreamReader(dulieu, Charset.forName("UTF8")));
			config.setDefaults(macdinh);
		}
	}

	public FileConfiguration lay() {
		if (this.config == null) {
			taiLai();
		}
		try {
			file = new FileInputStream(new File(plugin.getDataFolder(), "caidat.yml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		config = YamlConfiguration.loadConfiguration(new InputStreamReader(file, Charset.forName("UTF8")));
		return config;
	}

	public FileConfiguration ghi() {
		if (this.config == null) {
			taiLai();
		}
		config = YamlConfiguration.loadConfiguration(tep);
		return config;
	}

	public void luu() {

		if (config == null || tep == null)
			return;

		try {
			this.lay().save(tep);
		} catch (IOException e) {
			plugin.getServer().getLogger().log(Level.WARNING, "Không thể lưu vào" + tep, e);
		}
	}
}
