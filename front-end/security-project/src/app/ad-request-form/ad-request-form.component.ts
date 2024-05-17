import { Component, OnInit} from '@angular/core';
import { AdRequest } from '../model/adRequest.model';
import { UserService } from '../services/user.service';
import { UserRole } from '../model/userRole.model';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ad-request-form',
  templateUrl: './ad-request-form.component.html',
  styleUrls: ['./ad-request-form.component.css']
})
export class AdRequestFormComponent implements OnInit{
  adRequest: AdRequest = {
    email: '',
    deadline: new Date(),
    activeFrom: new Date(),
    activeTo: new Date(),
    description: ''
  };

  constructor(private userService: UserService, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);
    if (userRoles.length === 0) {
      this.router.navigate(['/']);
    }
    else if (!userRoles.includes(UserRole.CLIENT)) {
      this.router.navigate(['/homepage']); 
    }
  }

  onSubmit() {
    this.userService.createAdRequest(this.adRequest).subscribe(
      (response) => {
        console.log('Ad request created successfully');
        // Dodajte ovde logiku za obradu uspešnog odgovora ako je potrebno
      },
      (error) => {
        console.error('Error creating ad request:', error);
        // Dodajte ovde logiku za obradu greške ako je potrebno
      }
    );
  }
}
