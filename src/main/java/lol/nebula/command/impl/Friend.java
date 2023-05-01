package lol.nebula.command.impl;

import lol.nebula.Nebula;
import lol.nebula.command.Command;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class Friend extends Command {
    public Friend() {
        super(new String[]{"friend"}, "Adds or removes friends", "<username>");
    }

    @Override
    public String dispatch(String[] args) {
        if (args.length == 0) return getSyntax();

        String username = args[0];
        if (Nebula.getInstance().getFriends().isFriend(username)) {
            Nebula.getInstance().getFriends().removeFriend(username);
            return "Removed friend \"" + username + "\"";
        } else {
            Nebula.getInstance().getFriends().addFriend(username);
            return "Added friend \"" + username + "\"";
        }
    }
}
