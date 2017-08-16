package modules;

import checkers.Checker;
import checkers.impl.AccountChecker;
import checkers.impl.BalanceChecker;
import checkers.impl.LimitChecker;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import jobs.OnStartup;

public class OnStartupModule extends AbstractModule {
    @Override
    public void configure() {
        bind(OnStartup.class).asEagerSingleton();

        Multibinder<Checker> multibinder = Multibinder.newSetBinder(binder(), Checker.class);

        // If we wanted to extend the business rules, adding new checkers will be easy, we'll only need to add
        // a new implementation into checkers.impl package and add a binding here. The
        // services.impl.MoneyTransferServiceImpl will then automatically pick up and apply the new checker to requests
        multibinder.addBinding().to(AccountChecker.class);
        multibinder.addBinding().to(BalanceChecker.class);
        multibinder.addBinding().to(LimitChecker.class);
    }
}