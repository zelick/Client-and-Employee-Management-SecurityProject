import { Component, OnInit } from '@angular/core';
import { AdRequest } from '../model/adRequest.model';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-all-ad-requests',
  templateUrl: './all-ad-requests.component.html',
  styleUrls: ['./all-ad-requests.component.css']
})
export class AllAdRequestsComponent implements OnInit{
  allAdRequests: AdRequest[] = [];

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
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
