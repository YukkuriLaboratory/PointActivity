# PointActivity

運動量をカウントするためのFabricMod

# 前提MOD

- [ModMenu](https://modrinth.com/mod/modmenu)
- [Cloth Config](https://modrinth.com/mod/cloth-config)

# コマンド

- `pa point set {integer}`  
  行動ポイントを設定します
- `pa point get`  
  現在の行動ポイントを取得します(デバッグ用)
- `pa sync {player}`
  対象のポイントと同期します(spectatorモードでのみ有効)

# 開発ルール

- コミットメッセージやPRのタイトルは先頭に変更内容と合致する[gitmoji](https://gitmoji.dev)を付けてください(
  IDEAには[拡張機能](https://plugins.jetbrains.com/plugin/12383-gitmoji-plus-commit-button)もあります)
- 作業を行う際は新たなブランチを作成して、適宜PRを作成してください(mainブランチに直接コードをプッシュしないでください)
- コードの精査はCheckstyleを使用しています。gradleの`check`タスクが通ることを確認してください(まだ調整段階のため、この指摘は余計じゃないかってやつがあったら教えてください)