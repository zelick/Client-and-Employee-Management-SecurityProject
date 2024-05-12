import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PasswordlessLoginComponent } from './passwordless-login/passwordless-login.component';

const routes: Routes = [
  { path: '', component: PasswordlessLoginComponent }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
