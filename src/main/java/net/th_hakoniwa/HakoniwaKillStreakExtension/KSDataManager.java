package net.th_hakoniwa.HakoniwaKillStreakExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.th_hakoniwa.HakoniwaCore.API.HakoniwaAPI;
import net.th_hakoniwa.HakoniwaCore.Core.Player.HCPlayer;

/**
 * @author Kyan
 *
 */
public class KSDataManager {
	//Singleton instance
	private static KSDataManager instance = null;

	//KillStreak count
	private Map<Player, Integer> killStreakCount = new HashMap<>();


	private KSDataManager() {}

	/**
	 * @return Singleton instance of KSDataManager.
	 */
	public static KSDataManager getInstance() {
		if(instance == null) {
			instance = new KSDataManager();
		}
		return instance;
	}


	/**
	 * @param p Target player
	 */
	public void onJoin(Player p) {
		//プレイヤー参加時に呼ばれる
		int count = getKSCountFromDB(p.getUniqueId());
		//ロードチェック
		if(count == -1) {
			//ロードできなかった
			return;
		}
		//ロード成功
		//メモリ上のマップにデータを展開
		setKSCount(p, count);
	}

	/**
	 * @param p Target player
	 */
	public void onQuit(Player p) {
		//プレイヤー切断時に呼ばれる
		int count = getKSCount(p);
		//ロードチェック
		if(count == -1) {
			//データが無かった
			return;
		}
		//データベースへ保存
		setKSCountToDB(p.getUniqueId(), count);
		//マップから削除
		killStreakCount.remove(p);
	}


	/**
	 * @param p Target player
	 * @param count Killstreak count
	 */
	public void setKSCount(Player p, int count) {
		killStreakCount.put(p, count);
	}

	/** Update database.
	 * @param uid Player uuid
	 * @param count Killstreak count
	 */
	private void setKSCountToDB(UUID uid, int count) {
		//ロード処理したかどうかのフラグ
		boolean load = false;
		//プレイヤーデータ
		HCPlayer hcp = HakoniwaAPI.getPlayer(uid);
		//ロード済みチェック
		if(hcp == null) {
			//新規ロード
			HakoniwaAPI.loadPlayer(uid);
			//再取得
			hcp = HakoniwaAPI.getPlayer(uid);
			//失敗チェック
			if(hcp == null) {
				//失敗した データが無い
				Bukkit.getServer().getLogger().warning("[HKS] Player data not found. (UUID: " + uid.toString() + ")");
				return;
			}
			//フラグ上げる
			load = true;
		}

		//データを設定
		hcp.setData("KillStreakCount", count);
		//保存
		hcp.save();

		//アンロードチェック
		if(load) {
			//新規でロードしていた アンロード
			HakoniwaAPI.unloadPlayer(uid);
		}
	}


	/**
	 * @param p Target player
	 * @return Killstreak count
	 */
	public int getKSCount(Player p) {
		//処理対象外なら-1を返す
		if(!killStreakCount.containsKey(p)) return -1;
		//mapからデータ取得
		return killStreakCount.get(p);
	}

	/**
	 * @param uid Player uuid
	 * @return Player killstreak count (from database)
	 */
	private int getKSCountFromDB(UUID uid) {
		//ロード処理したかどうかのフラグ
		boolean load = false;
		//プレイヤーデータ
		HCPlayer hcp = HakoniwaAPI.getPlayer(uid);
		//ロード済みチェック
		if(hcp == null) {
			//新規ロード
			HakoniwaAPI.loadPlayer(uid);
			//再取得
			hcp = HakoniwaAPI.getPlayer(uid);
			//失敗チェック
			if(hcp == null) {
				//失敗した データが無い
				Bukkit.getServer().getLogger().warning("[HKS] Player data not found. (UUID: " + uid.toString() + ")");
				//プレイヤー自体のデータが無い場合は-1を返す
				return -1;
			}
			//フラグ上げる
			load = true;
		}

		//データを取得
		int result = hcp.getDataInt("KillStreakCount");
		//保存
		hcp.save();

		//アンロードチェック
		if(load) {
			//新規でロードしていた アンロード
			HakoniwaAPI.unloadPlayer(uid);
		}

		//リターン
		return result;
	}
}
