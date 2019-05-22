import {RouterModule, Routes} from '@angular/router';
import {ModuleWithProviders} from '@angular/core';
import {FrontPageComponent} from "./front-page/front-page.component";
import {AccountComponent} from "./account/account.component";
import {StatisticsComponent} from "./statistics/statistics.component";
import {ForgotPasswordComponent} from "./forgot-password/forgot-password.component";
import {SettingsComponent} from "./settings/settings.component";
import {VerificationComponent} from "./verification/verification.component";
import {AuthGuard} from "./guards/auth.guard";

const APP_ROUTES: Routes = [
    {path: '', component: FrontPageComponent},
    {path: 'account', component: AccountComponent, canActivate: [AuthGuard]},
    {path: 'statistics', component: StatisticsComponent, canActivate: [AuthGuard]},
    {path: 'settings', component: SettingsComponent, canActivate: [AuthGuard]},
    {path: 'reset-password', component: ForgotPasswordComponent},
    {path: 'verification', component: VerificationComponent},
    { path: '**', redirectTo: '/statistics', pathMatch: 'full' }
];
export const AppRouting: ModuleWithProviders = RouterModule.forRoot(APP_ROUTES);
