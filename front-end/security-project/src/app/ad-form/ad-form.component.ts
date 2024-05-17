import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AdRequest } from '../model/adRequest.model';
import { UserService } from '../services/user.service';
import { User } from '../model/user.model';
import { Ad } from '../model/ad.model';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-ad-form',
  templateUrl: './ad-form.component.html',
  styleUrls: ['./ad-form.component.css']
})
export class AdFormComponent implements OnInit {
  adRequest!: AdRequest;
  client!: User;
  description: string = '';
  slogan: string = '';

  constructor(private route: ActivatedRoute, private userService: UserService, private auth: AuthService, private router: Router) {}

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
      this.route.paramMap.subscribe(params => {
        const id = Number(params.get('id')); 
        if (!isNaN(id)) {
          this.userService.getAdRequestById(id).subscribe(adRequest => {
            this.adRequest = adRequest;
            console.log(adRequest);
            this.findUser();
          });
        }
      });
    }
  }

  findUser(): void{
    this.userService.findUserByEmail().subscribe(user => {
      this.client = user;
      console.log(user);
    });
  }

  onSubmit(): void {
    const ad: Ad = { 
      email: this.client.email,
      name: this.client.name,
      surname: this.client.surname, 
      activeFrom: this.adRequest.activeFrom, 
      activeTo: this.adRequest.activeTo, 
      description: this.adRequest.description, 
      slogan: this.slogan 
    };
    console.log(ad);

    this.userService.createAd(ad).subscribe(response => {
      console.log(ad);
      console.log(response);
    });
  }

}
