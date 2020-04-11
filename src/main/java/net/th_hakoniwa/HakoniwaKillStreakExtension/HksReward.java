package net.th_hakoniwa.HakoniwaKillStreakExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class HksReward {
	//付与時の全員表示メッセージ
	private List<String> broadcast = new ArrayList<>();
	//付与時の表示メッセージ
	private List<String> messages = new ArrayList<>();
	//付与アイテム
	private Map<Material, Integer> items = new HashMap<>();
	//コンソールコマンド
	private List<String> commands = new ArrayList<>();

	//ファイルとベースパスから生成
	public HksReward(FileConfiguration conf, String basePath) {
		if(conf.getStringList(basePath + ".Broadcast") != null) {
			broadcast.addAll(conf.getStringList(basePath + ".Broadcast"));
		}
		if(conf.getStringList(basePath + ".Message") != null) {
			messages.addAll(conf.getStringList(basePath + ".Message"));
		}

		if(conf.getConfigurationSection(basePath + ".Item") != null) {
			//アイテム設定がある ロード
			for(String key : conf.getConfigurationSection(basePath + ".Item").getKeys(false)) {
				Material m = Material.getMaterial(key);
				if(m != null) {
					//登録
					items.put(m, conf.getInt(basePath + ".Item." + key));
				}else {
					//不正なMaterial
					Bukkit.getServer().getLogger().warning("[HKS] Invalid material: " + key);
				}
			}
		}

		if(conf.getStringList(basePath + ".Command") != null) {
			commands.addAll(conf.getStringList(basePath + ".Command"));
		}
	}

	public List<String> getBroadcastMessage(){
		return broadcast;
	}

	public List<String> getMessages(){
		return messages;
	}

	public Map<Material, Integer> getItems(){
		return items;
	}

	public List<String> getCommands(){
		return commands;
	}
}
