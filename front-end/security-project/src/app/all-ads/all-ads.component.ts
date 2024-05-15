import { Component, OnInit } from '@angular/core';
import { Ad } from '../model/ad.model';
import { UserService } from '../services/user.service';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-all-ads',
  templateUrl: './all-ads.component.html',
  styleUrls: ['./all-ads.component.css']
})
export class AllAdsComponent implements OnInit{
  allAds: Ad[] = [];

  constructor(private userService: UserService, private auth: AuthService, private router: Router) { }

  ngOnInit(): void {
    const userRole = this.auth.getLoggedInUserRole(); 
      console.log(userRole);
      if (userRole !== "EMPLOYEE") {
        this.router.navigate(['/']);
      } else {
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

  createMore(): void{
    this.router.navigate(['/ad-requests']);
  }

}
