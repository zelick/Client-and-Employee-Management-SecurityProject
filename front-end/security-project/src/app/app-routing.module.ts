import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegistrationComponent } from './registration/registration.component';
import { RegistrationRequestsComponent } from './registration-requests/registrationRequests.component';
import { AdministratorProfileComponent } from './administrator/administrator-profile/administratorProfile.component';

const routes: Routes = [
  {path:'registration', component: RegistrationComponent}, 
  {path:'registrationRequests', component: RegistrationRequestsComponent}, 
  {path:'administratorProfile', component: AdministratorProfileComponent}, 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
