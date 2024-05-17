import { Component, OnInit } from '@angular/core';
import { Ad } from '../model/ad.model';
import { UserService } from '../services/user.service';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';

@Component({
  selector: 'app-all-ads',
  templateUrl: './all-ads.component.html',
  styleUrls: ['./all-ads.component.css']
})
export class AllAdsComponent implements OnInit{
  allAds: Ad[] = [];

  constructor(private userService: UserService, private auth: AuthService, private router: Router) { }

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);
    if (userRoles.length === 0) {
      this.router.navigate(['/']);
    }
    else if (!userRoles.includes(UserRole.EMPLOYEE)) {
      this.router.navigate(['/homepage']); 
    }
    else {
      this.getAllAds();
    }
  }

  getAllAds(): void{
    this.userService.getAllAds().subscribe(
      (data: Ad[]) => {
        this.allAds = data;
      },
      (error) => {
        console.error('Error fetching ad requests: ', error);
      }
    );
  }

}
