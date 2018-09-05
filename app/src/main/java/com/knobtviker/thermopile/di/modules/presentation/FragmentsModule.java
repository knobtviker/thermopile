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

    @ContributesAndroidInjector(modules = MainFragmentModule.class)
    abstract MainFragment contributeMainFragment();

    @ContributesAndroidInjector(modules = ChartsFragmentModule.class)
    abstract ChartsFragment contributeChartFragment();

    @ContributesAndroidInjector(modules = ScheduleFragmentModule.class)
    abstract ScheduleFragment contributeScheduleFragment();

    @ContributesAndroidInjector(modules = ThresholdFragmentModule.class)
    abstract ThresholdFragment contributeThresholdFragment();

    @ContributesAndroidInjector(modules = SettingsFragmentModule.class)
    abstract SettingsFragment contributeSettingsFragment();

    @ContributesAndroidInjector(modules = UnitsFragmentModule.class)
    abstract UnitsFragment contributeUnitsFragment();

    @ContributesAndroidInjector(modules = LocaleFragmentModule.class)
    abstract LocaleFragment contributeLocaleFragment();

    @ContributesAndroidInjector(modules = StyleFragmentModule.class)
    abstract StyleFragment contributeStyleFragment();

    @ContributesAndroidInjector(modules = NetworkFragmentModule.class)
    abstract NetworkFragment contributeNetworkFragment();

    @ContributesAndroidInjector(modules = WirelessFragmentModule.class)
    abstract WirelessFragment contributeWirelessFragment();

    @ContributesAndroidInjector(modules = ScreenSaverFragmentModule.class)
    abstract ScreenSaverFragment contributeScreensaverFragment();

    @ContributesAndroidInjector(modules = BlankFragmentModule.class)
    abstract BlankFragment contributeBlankFragment();
}
