import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.ArrayList;
import java.util.List;

public class Main extends ListenerAdapter
{
    public static String[ ] a;
    public static List<TextChannel> tcs = new ArrayList<>();
    public static List<TextChannel> parents = new ArrayList<>();
    public static void main(String[] args)
    {
        a = args;
        System.out.println("Bot is starting up.");
        JDA jda = JDABuilder.createDefault(args[0])
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new Main())
                .build();
        jda.updateCommands().addCommands(Commands.slash("sharechat", "チャンネルのメッセージを他のチャンネルと共有します。").addSubcommands(
                new SubcommandData("add", "共有チャンネルを追加します。"),
                new SubcommandData("remove", "共有チャンネルから削除します。")
        )).queue();
    }

    public void onReady(ReadyEvent e)
    {
        for (Guild guild : e.getJDA().getGuilds())
        {
            for (TextChannel textChannel : guild.getTextChannels())
            {
                if(textChannel.retrieveWebhooks().complete().stream().anyMatch(webhook -> webhook.getName().equals("sharechat")))
                {
                    tcs.add(textChannel);
                }
                if(textChannel.retrieveWebhooks().complete().stream().anyMatch(webhook -> webhook.getName().equals("sharechat-parent")))
                {
                    parents.add(textChannel);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void onMessageReceived(MessageReceivedEvent e)
    {
        if(!e.getAuthor().isBot() && e.getChannel().asTextChannel().retrieveWebhooks().complete().stream().anyMatch(webhook -> webhook.getName().equals("sharechat-parent")))
        {
            Message tar = e.getMessage().getReferencedMessage();
                e.getJDA().getTextChannelById(tar.getContentRaw().split("/")[5]).retrieveMessageById(tar.getContentRaw().split("/")[6])
                                .complete().replyEmbeds(new EmbedBuilder().setAuthor(e.getAuthor().getName(), null, e.getAuthor().getEffectiveAvatarUrl()).setDescription(e.getMessage().getContentRaw()).setFooter(e.getGuild().getName(), e.getGuild().getIconUrl()).build()).queue(message -> e.getMessage().addReaction(Emoji.fromFormatted(":white_check_mark:")).queue());
        }
        else if(!e.getAuthor().isBot() && e.getChannel().asTextChannel().retrieveWebhooks().complete().stream().anyMatch(webhook -> webhook.getName().equals("sharechat")))
        {
            for (TextChannel tc : parents)
            {
                tc.sendMessage(e.getMessage().getJumpUrl()).setEmbeds(new EmbedBuilder().setAuthor(e.getAuthor().getName(), null, e.getAuthor().getEffectiveAvatarUrl()).setDescription(e.getMessage().getContentRaw()).setFooter(e.getGuild().getName(), e.getGuild().getIconUrl()).build()).queue();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e)
    {
        if ("sharechat".equals(e.getName()))
        {
            switch (e.getSubcommandName())
            {
                case "add" ->
                {

                    if (e.getGuild().getId().equals("970296625786388530") && e.getChannel().asTextChannel().retrieveWebhooks().complete().stream().noneMatch(webhook -> webhook.getName().contains("sharechat")))
                    {
                        e.getChannel().asTextChannel().createWebhook("sharechat-parent").queue();
                        parents.add(e.getChannel().asTextChannel());
                        e.reply("✅ 親チャンネルを登録しました。解除するには、`/sharechat remove`を実行してください。").setEphemeral(true).queue();
                    }
                    else if (e.getChannel().asTextChannel().retrieveWebhooks().complete().stream().noneMatch(webhook -> webhook.getName().contains("sharechat")))
                    {
                        e.getChannel().asTextChannel().createWebhook("sharechat").queue();
                        tcs.add(e.getChannel().asTextChannel());
                        e.reply("✅ 共有チャンネルを登録しました。解除するには、`/sharechat remove`を実行してください。").setEphemeral(true).queue();
                    }
                }
                case "remove" ->
                {
                    for (Webhook webhook : e.getChannel().asTextChannel().retrieveWebhooks().complete())
                    {
                        webhook.delete().queue();
                        tcs.remove(webhook.getChannel().asTextChannel());
                        parents.remove(webhook.getChannel().asTextChannel());
                    }
                    e.reply("✅ 共有チャンネルを解除しました。").setEphemeral(true).queue();
                }
            }
        }

    }
}