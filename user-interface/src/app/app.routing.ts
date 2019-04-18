import {RouterModule, Routes} from '@angular/router';
import {ModuleWithProviders} from '@angular/core';
import {FrontPageComponent} from "./front-page/front-page.component";
import {AccountComponent} from "./account/account.component";
import {StatisticsComponent} from "./statistics/statistics.component";
import {ForgotPasswordComponent} from "./forgot-password/forgot-password.component";
import {SettingsComponent} from "./settings/settings.component";
import {VerificationComponent} from "./verification/verification.component";

const APP_ROUTES: Routes = [
    {path: '', component: FrontPageComponent},
    {path: 'account', component: AccountComponent},
    {path: 'statistics', component: StatisticsComponent},
    {path: 'settings', component: SettingsComponent},
    {path: 'reset-password', component: ForgotPasswordComponent},
    {path: 'verification', component: VerificationComponent}
];
export const AppRouting: ModuleWithProviders = RouterModule.forRoot(APP_ROUTES);
