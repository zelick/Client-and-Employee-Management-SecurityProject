import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PasswordlessLoginComponent } from './passwordless-login/passwordless-login.component';
import { LoginService } from './services/login.service';
import { RegistrationComponent } from './registration/registration.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AuthService } from './service/auth.service';
import { TokenInterceptor } from './interceptor/TokenInterceptor';
import { ApiService } from './service/api.service';
import { ConfigService } from './service/config.service';

import { RegistrationRequestsComponent } from './administrator/registration-requests/registrationRequests.component';
import { AdministratorProfileComponent } from './administrator/administrator-profile/administratorProfile.component';
import { AllEmployeesComponent } from './administrator/all-employees/allEmployees.component';
import { AllClientsComponent } from './administrator/all-clients/allClients.component';
import { LoginComponent } from './login/login.component';
import { NavbarComponent } from './navbar/navbar.component';
import { ClientProfileComponent } from './client-profile/client-profile.component';
import { EditClientProfileComponent } from './edit-client-profile/edit-client-profile.component';
import { AdRequestFormComponent } from './ad-request-form/ad-request-form.component';
import { AllAdRequestsComponent } from './all-ad-requests/all-ad-requests.component';
import { AdFormComponent } from './ad-form/ad-form.component';
import { AllAdsComponent } from './all-ads/all-ads.component';
import { AllClientAdsComponent } from './all-client-ads/all-client-ads.component';
import { UserService } from './services/user.service';
import { HomePageComponent } from './homepage/homepage.component';
import { PermissionsManipulationComponent } from './administrator/permissions-manipulation/permissionsManipulation.component';
import { ClientHomepageComponent } from './client-homepage/client-homepage.component';
import { NotificationComponent } from './notification/notification.component';
import { AllUsersComponent } from './administrator/all-users/all-users.component';
import { ResetPasswordComponent } from './administrator/reset-password/reset-password.component';
import { NgxCaptchaModule } from 'ngx-captcha';

@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    PasswordlessLoginComponent,
    RegistrationComponent,
    RegistrationRequestsComponent,
    AdministratorProfileComponent,
    AllEmployeesComponent,
    AllClientsComponent,
    LoginComponent,
    NavbarComponent,
    ClientProfileComponent,
    EditClientProfileComponent,
    AdRequestFormComponent,
    AllAdRequestsComponent,
    AdFormComponent,
    AllAdsComponent,
    AllClientAdsComponent,
    HomePageComponent,
    PermissionsManipulationComponent,
    ClientHomepageComponent,
    NotificationComponent,
    AllUsersComponent,
    ResetPasswordComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgxCaptchaModule
  ],
  providers: [
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    ApiService,
    UserService,
    ConfigService,
    LoginService,
    /*[{
      provide: RECAPTCHA_V3_SITE_KEY,
      useValue: environment.recaptcha.siteKey,
    }]
    */
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
