import { Component, OnInit } from '@angular/core';
import { Ad } from '../model/ad.model';
import { UserService } from '../services/user.service';
import { UserRole } from '../model/userRole.model';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { User } from '../model/user.model';

@Component({
  selector: 'app-all-client-ads',
  templateUrl: './all-client-ads.component.html',
  styleUrls: ['./all-client-ads.component.css']
})
export class AllClientAdsComponent implements OnInit{
  allAds: Ad[] = [];
  loggedUser!: User;

  constructor(private userService: UserService, private auth: AuthService, private router: Router) { }

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);
    if (userRoles.length === 0) {
      this.router.navigate(['/']);
    }
    else if (!userRoles.includes(UserRole.CLIENT)) {
      this.router.navigate(['/homepage']); 
    }
    else {
      this.getLoggedInUser();
    }
  }

  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.loggedUser = user;
        console.log('ROLA ULOGOVANOG KORISNIKA: ' + this.loggedUser.roles);
        localStorage.setItem('loggedUserRole', this.loggedUser.roles.join(','));
        this.getAllAdsByEmail();
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }

  getAllAdsByEmail(): void{
    this.userService.getAllAdsByEmail(this.loggedUser.email).subscribe(
      (data: Ad[]) => {
        this.allAds = data;
      },
      (error) => {
        console.error('Error fetching ad requests: ', error);
      }
    );
  }
}