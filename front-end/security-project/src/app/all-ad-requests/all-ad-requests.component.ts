import { Component, OnInit } from '@angular/core';
import { AdRequest } from '../model/adRequest.model';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-all-ad-requests',
  templateUrl: './all-ad-requests.component.html',
  styleUrls: ['./all-ad-requests.component.css']
})
export class AllAdRequestsComponent implements OnInit{
  allAdRequests: AdRequest[] = [];

  constructor(private userService: UserService, private router: Router, private auth: AuthService) { }

  ngOnInit(): void {
    const userRole = this.auth.getLoggedInUserRole(); 
      console.log(userRole);
      if (userRole !== "EMPLOYEE") {
        this.router.navigate(['/']);
      } else {
        this.getAllAdRequests();
      }
  }

  getAllAdRequests(): void{
    this.userService.getAllAdRequests().subscribe(
      (data: AdRequest[]) => {
        this.allAdRequests = data;
      },
      (error) => {
        console.error('Error fetching ad requests: ', error);
      }
    );
  }

  createAd(adRequest: AdRequest)
  {
    this.router.navigate(['/ad-form/' + adRequest.id]);
  }
}
