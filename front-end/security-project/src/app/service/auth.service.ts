import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ApiService } from "./api.service";
import { Router } from "@angular/router";
import { ConfigService } from "./config.service";
import {catchError, map, switchMap, tap} from 'rxjs/operators';
import { UserService } from "../services/user.service";
import { User } from "../model/user.model";
import { UserRole } from "../model/userRole.model";
import { EMPTY, Observable, interval } from 'rxjs';

@Injectable()
export class AuthService {

  constructor(
    private apiService: ApiService,
    private userService: UserService,
    private config: ConfigService,
    private router: Router, 
    private http: HttpClient
  ){
  }

  user: User | undefined;

  private _api_url = 'http://localhost:8080/api/auth';
  private access_token = null;

  login(user:any) {
    const loginHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });
    // const body = `username=${user.username}&password=${user.password}`;
    const body = {
      'username': user.username,
      'password': user.password
    };
    console.log("username:", user.username);
    console.log("password:", user.password);
    console.log("putanja:",this.config.login_url );

    return this.apiService.post(this.config.login_url, JSON.stringify(body), loginHeaders)
    .pipe(map((res) => {
      console.log('Login success');
      if (res.body && res.body.accessToken) {
        this.access_token = res.body.accessToken;
        localStorage.setItem("jwt", res.body.accessToken);
        localStorage.setItem("refreshToken", res.body.refreshToken);
        console.log("Acces token:", this.access_token);
        console.log("Refresh token:", this.refreshToken);
        this.getLoggedInUser();
        
        this.startTokenRefreshCheck(); //poziv za refresh token

        this.router.navigate(['/homepage']);
      } else {
        console.error('Invalid response or missing access token:', res);
      }
    }));
  }

  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.user = user;
        console.log('ROLA ULOGOVANOG KORISNIKA: ' + this.user.roles);
        localStorage.setItem('loggedUserRole', this.user.roles.join(','));
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }

  getLoggedInUserRoles(): UserRole[] {
    const rolesString = localStorage.getItem('loggedUserRole');
    if (!rolesString) {
        return []; 
    }
    return rolesString.split(',').map(role => role.trim()) as UserRole[];
}

  signup(user:any) { //NE TREBA OVO 
    const signupHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });
    return this.apiService.post(this.config.signup_url, JSON.stringify(user), signupHeaders)
      .pipe(map(() => {
        console.log('Sign up success');
      }));
  }

  private refreshCheckInterval: any; //Promenljiva za interval   
  logout() {
    //this.userService.currentUser = null;
    localStorage.removeItem("loggedUserRole")
    localStorage.removeItem("refreshToken") //dodala
    localStorage.removeItem("jwt");
    this.access_token = null;
    console.log("KAD SE ODJAVI: " + localStorage.getItem("loggedUserRole"));

    // Zaustavi interval provere tokena
    if (this.refreshCheckInterval) {
      console.log("USAO unsubscribe")
      this.refreshCheckInterval.unsubscribe(); //ovo NE RADI, nez sto?
    }
    this.router.navigate(['/']);
  }

  tokenIsPresent() {
    return localStorage.getItem('jwt') !== null;
  }
  
  getToken() {
    return localStorage.getItem('jwt');
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('jwt');
    return !!token; 
  }

  private readonly refreshTokenEndpoint = '/refresh-token';
  private readonly checkTokenEndpoint = '/check-token';

  startTokenRefreshCheck(): void {
    this.refreshCheckInterval = interval(5000) // Every 5 seconds
      .pipe(
        switchMap(() => this.checkTokenValidity()),
        catchError(() => this.refreshToken())
      )
      .subscribe(
        () => console.log('Token is valid.'),
        () => console.log('Token is not valid. Refresh token is being triggered.'),
        () => {
          console.log('Token refresh completed. Resuming token validity check.');
          // After token refresh, resume token validity check
          this.startTokenRefreshCheck();
        }
      );
  }

  checkTokenValidity(): Observable<void> {
    console.log('-----------------CHECKING TOKEN EXPIRY.-----------------');
  
    const accessToken = localStorage.getItem('jwt');
    if (!accessToken) {
      console.error('Access token not found in local storage.');
      return EMPTY;  // Return an empty Observable if accessToken is not present
    }
  
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${accessToken}`
    });
  
    return this.http.get<void>(`${this._api_url}/check-token`, { headers });
  }
  
  refreshToken(): Observable<any> {
    console.log('CALLING FOR ACCESS TOKEN REFRESH!!!!!.');
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      console.error('Refresh token not found in local storage.');
      return EMPTY;  // Return an empty Observable if refreshToken is not present
    }
  
    return this.http.get<any>(`${this._api_url}/refresh-token`, { headers: { 'Authorization': `Bearer ${refreshToken}` } })
      .pipe(
        tap((res) => {
          if (res && res.accessToken) {
            console.log("---NOVI ACCESS TOKEN---", res.accessToken )
            localStorage.setItem('jwt', res.accessToken); // Update the new access token in local storage
          }
        }),
        catchError((error) => {
          // Check if the error is 401 (Unauthorized), which means the refreshToken has expired
          if (error.status === 401) {
            console.error('Refresh token has expired.');
            // Log out the user and redirect to the home page
            this.logout();
            this.router.navigate(['/']); 
          }
          return EMPTY; // Return an empty Observable if refreshToken fails
        })
      );
  }


}