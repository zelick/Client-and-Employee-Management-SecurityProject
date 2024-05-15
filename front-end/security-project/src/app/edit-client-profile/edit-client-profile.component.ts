import { Component, OnInit } from '@angular/core';
import { User } from '../model/user.model';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-client-profile',
  templateUrl: './edit-client-profile.component.html',
  styleUrls: ['./edit-client-profile.component.css']
})
export class EditClientProfileComponent implements OnInit{
  user!: User;

  constructor ( private activatedRoute : ActivatedRoute, private userService : UserService, private router : Router) {}

  ngOnInit(): void {
    this.activatedRoute.paramMap.subscribe(params => {
      const email = params.get('email');
      console.log("Email:" + email);
      if (email) {
        this.userService.findUserByEmail().subscribe(
          (user: User) => {
            console.log(user);
            this.user = user; // Postavljanje vrednosti korisnika za uređivanje
          },
          (error) => {
            console.error('Error fetching user:', error);
          }
        );
      }
    });
  }

  onSubmit(): void {
    this.userService.updateClient(this.user).subscribe(
      (response: any) => {
        console.log('User updated successfully:', response);
        // Možete dodati poruku ili preusmeriti korisnika na drugu stranicu
        this.router.navigate(['/client-profile']);
      },
      (error) => {
        console.error('Error updating user:', error);
        // Možete prikazati odgovarajuću poruku o grešci
      }
    );
  }
  
  
}
