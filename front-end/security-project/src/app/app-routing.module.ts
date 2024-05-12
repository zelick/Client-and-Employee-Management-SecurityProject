import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegistrationComponent } from './registration/registration.component';
import { RegistrationRequestsComponent } from './registration-requests/registrationRequests.component';

const routes: Routes = [
  {path:'registration', component: RegistrationComponent}, 
  {path:'registrationRequests', component: RegistrationRequestsComponent}, 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
