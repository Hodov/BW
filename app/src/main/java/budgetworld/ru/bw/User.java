package budgetworld.ru.bw;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andrey on 30.01.2016.
 */
public class User {


    public String userName = null;
    public String userEmail = null;
    public String userPhone = null;
    private Context activity;

    public User(Context activity) {
        this.activity = activity;
        getUser();
    }

    private void getUser() {
        AccountManager manager = AccountManager.get(activity);

        Account[] accountsVK = manager.getAccountsByType("com.vkontakte.account");
        Account[] accountsViber = manager.getAccountsByType("com.viber.voip");
        Account[] accountsGoogle = manager.getAccountsByType("com.google");

        if (accountsVK.length > 0) {
            userName = accountsVK[0].name;
        }

        if (accountsGoogle.length > 0) {
            userEmail = accountsGoogle[0].name;
        }

        if (accountsViber.length > 0) {
            userPhone = accountsViber[0].name;
        }
    }

}
