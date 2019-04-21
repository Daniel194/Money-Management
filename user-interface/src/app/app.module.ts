import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MaterialModule} from "./material.modules";
import {AppRouting} from "./app.routing";
import {AccountConnectionComponent} from "./front-page/account-section/account-connection/account-connection.component";
import {FrontPageComponent} from './front-page/front-page.component';
import {HttpClientModule} from "@angular/common/http";
import {ToastrModule} from "ngx-toastr";
import {AccountComponent} from './account/account.component';
import {StatisticsComponent} from './statistics/statistics.component';
import {ItemDialogComponent} from './item-dialog/item-dialog.component';
import {NgxChartsModule} from "@swimlane/ngx-charts";
import {DateFormatPipe} from "./pipe/DateFormatPipe";
import { SocialMediaConnectionComponent } from './front-page/account-section/social-media-connection/social-media-connection.component';
import { AccountSectionComponent } from './front-page/account-section/account-section.component';
import { AccountTroubleComponent } from './front-page/account-section/account-trouble/account-trouble.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { SettingsComponent } from './settings/settings.component';
import { VerificationComponent } from './verification/verification.component';

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        MaterialModule,
        ToastrModule.forRoot({
            positionClass: 'toast-bottom-right',
            timeOut: 10000,
            extendedTimeOut: 10000
        }),
        AppRouting,
        NgxChartsModule
    ],
    declarations: [
        AppComponent,
        AccountConnectionComponent,
        FrontPageComponent,
        AccountComponent,
        StatisticsComponent,
        ItemDialogComponent,
        DateFormatPipe,
        SocialMediaConnectionComponent,
        AccountSectionComponent,
        AccountTroubleComponent,
        ForgotPasswordComponent,
        SettingsComponent,
        VerificationComponent
    ],
    bootstrap: [AppComponent],
    providers: [
        DateFormatPipe
    ],
    entryComponents: [
        ItemDialogComponent
    ]
})
export class AppModule {
}
