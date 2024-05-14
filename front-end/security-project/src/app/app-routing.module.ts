import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PasswordlessLoginComponent } from './passwordless-login/passwordless-login.component';
import { RegistrationComponent } from './registration/registration.component';
import { RegistrationRequestsComponent } from './registration-requests/registrationRequests.component';
import { ClientProfileComponent } from './client-profile/client-profile.component';
import { EditClientProfileComponent } from './edit-client-profile/edit-client-profile.component';
import { AdRequestFormComponent } from './ad-request-form/ad-request-form.component';
import { EmployeeProfileComponent } from './employee-profile/employee-profile.component';
import { EditEmployeeProfileComponent } from './edit-employee-profile/edit-employee-profile.component';
import { AllAdRequestsComponent } from './all-ad-requests/all-ad-requests.component';

const routes: Routes = [
  {path:'registration', component: RegistrationComponent}, 
  {path:'registrationRequests', component: RegistrationRequestsComponent}, 
  { path: 'login', component: PasswordlessLoginComponent },
  { path: 'client-profile', component: ClientProfileComponent},
  { path: 'edit-client-profile/:email', component: EditClientProfileComponent },
  { path: 'ad-request-form', component: AdRequestFormComponent},
  { path: 'employee-profile', component: EmployeeProfileComponent},
  { path: 'edit-employee-profile/:email', component: EditEmployeeProfileComponent},
  { path: 'ad-requests', component: AllAdRequestsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
