# ShareChatBot1

**不具合を防止するため、ボットには管理者権限を付与しておいてください。**

# 起動方法

**あらかじめJava17以降をインストールしておいてください。**

**https://www.oracle.com/jp/java/technologies/downloads/#java17**

**https://discord.com/developers のボットページで特権インテント（Privileged Gateway Intent）の3つのスイッチをONにしてください。**

Windowsの方はrun.bat、Linuxの方はrun.sh（要実行権限）を以下の通り編集してから実行してください。

元のファイル→`java -jar ShareChatBot1.jar token`

ファイル内の文字列を以下のように置き換えてください。

token→ボットトークン

### 機能一覧

`/sharechat add` → 共有チャットにチャンネルを追加します。

`/sharechat remove` → 共有チャットからチャンネルを削除します。

### 動作条件

ボットにウェブフックの管理権限、埋め込みリンク権限、メッセージ送信権限があること。

テキストチャンネルであること（チャンネルのマークが「＃」になっていること）

これらの条件に満たしていない場合はエラーとなります。