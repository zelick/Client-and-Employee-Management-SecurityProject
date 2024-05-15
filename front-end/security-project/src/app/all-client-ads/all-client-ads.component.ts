import { Component, OnInit } from '@angular/core';
import { Ad } from '../model/ad.model';
import { UserService } from '../services/user.service';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
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
    const userRole = this.auth.getLoggedInUserRole(); // Poziv metode da dobijete stvarnu vrednost uloge
      console.log(userRole);
      if (userRole !== "CLIENT") {
        this.router.navigate(['/']);
      } else {
        this.getLoggedInUser();
      }
  }

  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.loggedUser = user;
        console.log('ROLA ULOGOVANOG KORISNIKA: ' + this.loggedUser.role);
        localStorage.setItem('loggedUserRole', this.loggedUser.role);
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
