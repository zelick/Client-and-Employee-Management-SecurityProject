import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../model/user.model';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls:[ './navbar.component.css']
})
export class NavbarComponent implements OnInit {
  user!: User;
  
  constructor(
    private router: Router,
    private auth: AuthService) {
  }

  ngOnInit():void{
  }
    
  redirectToLogin() {
    this.router.navigate(['/']);
  }

  redirectToPasswordlessLogin() {
    this.router.navigate(['/login']);
  }

  logout(){
    this.auth.logout(); 
    this.router.navigate(['/']);
  }

  registration() {
    this.router.navigate(['/registration']);
  }

  administratorProfile() {
    this.router.navigate(['/administratorProfile']);
  }

  allUsers() {
    this.router.navigate(['/allUsers']);
  }

  allClients() {
    this.router.navigate(['/allClients']);
  }

  allEmployees() {
    this.router.navigate(['/allEmployees']);
  }

  registrationRequests() {
    this.router.navigate(['/registrationRequests']);
  }

  allAds() {
    this.router.navigate(['/ads']);
  }
  
  allAdRequests() {
    this.router.navigate(['/ad-requests'])
  }

  clientAds() {
    this.router.navigate(['/client-ads']);
  }

  clientProfile() {
    this.router.navigate(['/client-profile']);
  }

  createAdRequest() {
    this.router.navigate(['/ad-request-form']);
  }

  permissionsManipulation() {
    this.router.navigate(['/permissionsManipulation']);
  }

  getAllAdminNotifications() {
    this.router.navigate(['/notifications']);
  }
}
