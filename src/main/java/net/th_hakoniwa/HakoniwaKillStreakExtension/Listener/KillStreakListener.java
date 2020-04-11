package net.th_hakoniwa.HakoniwaKillStreakExtension.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.th_hakoniwa.HakoniwaKillStreakExtension.KSDataManager;
import net.th_hakoniwa.HakoniwaKillStreakExtension.KSRewardDirector;

public class KillStreakListener implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		//接続時データ取得
		KSDataManager.getInstance().onJoin(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		//切断時データ保存
		KSDataManager.getInstance().onQuit(e.getPlayer());
	}


	//TODO ここから実装が必要
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		//死んだプレイヤーのキルストカウントをリセット
		KSDataManager.getInstance().setKSCount(e.getEntity(), 0);
		//メッセージで通知
		e.getEntity().sendMessage("[§cHKS§f] キルストリークがリセットされました。");

		//殺したプレイヤーがnullじゃない、かつ同一ではない場合処理
		if(e.getEntity().getKiller() != null && !e.getEntity().equals(e.getEntity().getKiller())) {
			//キルしたプレイヤー
			Player killer = e.getEntity().getKiller();

			//カウント加算
			int count = KSDataManager.getInstance().getKSCount(killer) + 1;
			KSDataManager.getInstance().setKSCount(killer, count);

			//メッセージで通知
			killer.sendMessage("[§cHKS§f] 現在のキルストリークは" + count + "です。");

			//報酬配布
			KSRewardDirector.getInstance().checkReward(killer, count);
		}
	}
}
