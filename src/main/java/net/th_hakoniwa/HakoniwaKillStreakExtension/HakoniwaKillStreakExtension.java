package net.th_hakoniwa.HakoniwaKillStreakExtension;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.th_hakoniwa.HakoniwaCore.Extension.HakoniwaExtension;
import net.th_hakoniwa.HakoniwaKillStreakExtension.Listener.KillStreakListener;

public class HakoniwaKillStreakExtension extends HakoniwaExtension {
	private static Plugin plugin;
	private static File dataFolder;

	@Override
	public void onEnable() {
		//プラグインインスタンス取得
		plugin = getPlugin();
		//データフォルダ取得
		dataFolder = getDataFolder();
		//報酬データ読み込み
		KSRewardDirector.getInstance().initialize();
		//キルストデータ初期化
		KSDataManager.getInstance().initialize();
		//イベントリスナーを登録
		getPlugin().getServer().getPluginManager().registerEvents(new KillStreakListener(), getPlugin());

		//全員ログイン扱いにする
		for(Player p : Bukkit.getOnlinePlayers()) {
			KSDataManager.getInstance().onJoin(p);
		}
	}

	@Override
	public void onDisable() {
		//全員ログアウト扱いにする
		for(Player p : Bukkit.getOnlinePlayers()) {
			KSDataManager.getInstance().onQuit(p);
		}
	}

	//他クラスからプラグインインスタンスを取得する
	public static Plugin getHCPlugin() {
		return plugin;
	}

	//他クラスからデータフォルダを取得する
	public static File getExtDataFolder() {
		return dataFolder;
	}
}
