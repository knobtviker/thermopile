package com.knobtviker.thermopile.di.modules.presentation;

import com.knobtviker.thermopile.di.modules.presentation.fragments.BlankFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.ChartsFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.LocaleFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.MainFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.NetworkFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.ScheduleFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.ScreenSaverFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.SettingsFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.StyleFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.ThresholdFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.UnitsFragmentModule;
import com.knobtviker.thermopile.di.modules.presentation.fragments.WirelessFragmentModule;
import com.knobtviker.thermopile.presentation.fragments.BlankFragment;
import com.knobtviker.thermopile.presentation.fragments.ChartsFragment;
import com.knobtviker.thermopile.presentation.fragments.LocaleFragment;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;
import com.knobtviker.thermopile.presentation.fragments.NetworkFragment;
import com.knobtviker.thermopile.presentation.fragments.ScheduleFragment;
import com.knobtviker.thermopile.presentation.fragments.ScreenSaverFragment;
import com.knobtviker.thermopile.presentation.fragments.SettingsFragment;
import com.knobtviker.thermopile.presentation.fragments.StyleFragment;
import com.knobtviker.thermopile.presentation.fragments.ThresholdFragment;
import com.knobtviker.thermopile.presentation.fragments.UnitsFragment;
import com.knobtviker.thermopile.presentation.fragments.WirelessFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentsModule {

//    @ContributesAndroidInjector
//    abstract BaseFragment provideBaseFragment();

    @ContributesAndroidInjector(modules = MainFragmentModule.class)
    abstract MainFragment provideMainFragment();

    @ContributesAndroidInjector(modules = ChartsFragmentModule.class)
    abstract ChartsFragment provideChartFragment();

    @ContributesAndroidInjector(modules = ScheduleFragmentModule.class)
    abstract ScheduleFragment provideScheduleFragment();

    @ContributesAndroidInjector(modules = ThresholdFragmentModule.class)
    abstract ThresholdFragment provideThresholdFragment();

    @ContributesAndroidInjector(modules = SettingsFragmentModule.class)
    abstract SettingsFragment provideSettingsFragment();

    @ContributesAndroidInjector(modules = UnitsFragmentModule.class)
    abstract UnitsFragment provideUnitsFragment();

    @ContributesAndroidInjector(modules = LocaleFragmentModule.class)
    abstract LocaleFragment provideLocaleFragment();

    @ContributesAndroidInjector(modules = StyleFragmentModule.class)
    abstract StyleFragment provideStyleFragment();

    @ContributesAndroidInjector(modules = NetworkFragmentModule.class)
    abstract NetworkFragment provideNetworkFragment();

    @ContributesAndroidInjector(modules = WirelessFragmentModule.class)
    abstract WirelessFragment provideWirelessFragment();

    @ContributesAndroidInjector(modules = ScreenSaverFragmentModule.class)
    abstract ScreenSaverFragment provideScreensaverFragment();

    @ContributesAndroidInjector(modules = BlankFragmentModule.class)
    abstract BlankFragment provideBlankFragment();
}
