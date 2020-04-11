package net.th_hakoniwa.HakoniwaKillStreakExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KSRewardDirector {
	private static KSRewardDirector instance = null;

	//configインスタンス
	private YamlConfiguration conf;

	//報酬データ
	private Map<Integer, HksReward> rwOnce = new HashMap<>();
	private Map<Integer, HksReward> rwRepeat = new HashMap<>();


	private KSRewardDirector() {}

	public static KSRewardDirector getInstance() {
		if(instance == null) {
			instance = new KSRewardDirector();
		}
		return instance;
	}

	public void initialize() {
		//初期化
		conf = null;

		//configファイルの存在チェック
		File configFile = new File(HakoniwaKillStreakExtension.getExtDataFolder(), "ksconf.yml");
		//存在しなければ新規作成
		if(!configFile.exists()) {
			InputStream is = getClass().getClassLoader().getResourceAsStream("ksconf.yml");
			try {
				Files.copy(is, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Bukkit.getLogger().warning("[HKS] Failed to copy config file.");
				e.printStackTrace();
				return;
			}finally{
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//config読み込み
		conf = YamlConfiguration.loadConfiguration(configFile);

		//リワードデータ読み込み
		//一回だけ
		for(String key : conf.getConfigurationSection("Reward.Once").getKeys(false)) {
			int num;
			try {
				//数字にする
				num = Integer.parseInt(key);
				//読み込み
				HksReward data = new HksReward(conf, "Reward.Once." + key);
				//登録
				rwOnce.put(num, data);
			}catch(Exception e) {
				Bukkit.getLogger().warning("[HKS] Invalid count (Once): " + key);
			}
		}
		//繰り返し
		for(String key : conf.getConfigurationSection("Reward.Repeat").getKeys(false)) {
			int num;
			try {
				//数字にする
				num = Integer.parseInt(key);
				//読み込み
				HksReward data = new HksReward(conf, "Reward.Repeat." + key);
				//登録
				rwRepeat.put(num, data);
			}catch(Exception e) {
				Bukkit.getLogger().warning("[HKS] Invalid count (Repeat): " + key);
			}
		}
	}

	public void checkReward(Player p, int streak) {
		//単発チェック
		if(rwOnce.containsKey(streak)) {
			//配布実行
			giveReward(p, streak, rwOnce.get(streak));
		}
		//繰り返しチェック
		for(int key : rwRepeat.keySet()) {
			if(streak % key == 0) {
				//配布実行
				giveReward(p, streak, rwRepeat.get(key));
			}
		}
	}

	private void giveReward(Player p, int streak, HksReward reward) {
		//TODO 仮実装
		// 報酬配る
		Map<Material, Integer> items = reward.getItems();
		for(Material m : items.keySet()) {
			p.getInventory().addItem(new ItemStack(m, items.get(m)));
		}
		//コマンド実行する
		ConsoleCommandSender ccs = Bukkit.getConsoleSender();
		for(String cmd : reward.getCommands()) {
			Bukkit.dispatchCommand(ccs, cmd.replaceAll("%player%", p.getName()));
		}
		//メッセージ放送
		for(String bc : reward.getBroadcastMessage()) {
			String temp = ChatColor.translateAlternateColorCodes('&', bc);
			temp = temp.replaceAll("%player%", p.getName()).replaceAll("%streak%", String.valueOf(streak));
			Bukkit.getServer().broadcastMessage(temp);
		}
		//メッセージ送る
		for(String msg : reward.getMessages()) {
			String temp = ChatColor.translateAlternateColorCodes('&', msg);
			temp = temp.replaceAll("%player%", p.getName()).replaceAll("%streak%", String.valueOf(streak));
			p.sendMessage(temp);
		}
	}
}
