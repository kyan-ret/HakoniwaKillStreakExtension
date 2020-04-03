package net.th_hakoniwa.HakoniwaKillStreakExtension;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class KSConfigManager {
	//シングルトンインスタンス
	private static KSConfigManager instance = null;

	//configインスタンス
	private YamlConfiguration conf = null;

	//コンストラクタ 初期化処理はここでする
	private KSConfigManager() {
		//configファイルの存在チェック
		File configFile = new File(HakoniwaKillStreakExtension.getExtDataFolder(), "ksconf.yml");
		//存在しなければ新規作成
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				//ファイルが作れなかった
				e.printStackTrace();
			}
		}
		//config読み込み
		conf = YamlConfiguration.loadConfiguration(configFile);
	}


	public static KSConfigManager getInstance() {
		if(instance == null) {
			instance = new KSConfigManager();
		}
		return instance;
	}

	public YamlConfiguration getConfig() {
		return conf;
	}
}
