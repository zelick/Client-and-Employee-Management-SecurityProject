import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegistrationComponent } from './registration/registration.component';
import { RegistrationRequestsComponent } from './registration-requests/registrationRequests.component';
import { AdministratorProfileComponent } from './administrator/administrator-profile/administratorProfile.component';
import { AllEmployeesComponent } from './administrator/all-employees/allEmployees.component';

const routes: Routes = [
  {path:'registration', component: RegistrationComponent}, 
  {path:'registrationRequests', component: RegistrationRequestsComponent}, 
  {path:'administratorProfile', component: AdministratorProfileComponent}, 
  {path:'allEmployees', component: AllEmployeesComponent}, 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
