import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/env/environment';
import { User } from '../model/user.model';
import { Observable } from 'rxjs';
import { RegistrationRequestResponse } from '../model/registrationRequestResponse.model';
import { ResponseMessage } from '../model/responseMessage.model';
import { LoginReponse } from '../model/loginResponse.model';
import { AdRequest } from '../model/adRequest.model';
import { Ad } from '../model/ad.model';
import { UserRole } from '../model/userRole.model';
import { Permission } from '../model/permission.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly apiUrl = `${environment.apiHost}`;

  constructor(private http: HttpClient) {}

  //ADMIN

  getUserData(): Observable<User> {
    return this.http.get<User>(this.apiUrl + 'admins/getAdminData');
  }

  getAllEmployees(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl + 'admins/getAllEmployees');
  }

  getAllClients(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl + 'admins/getAllClients');
  }

  getAllRegistrationRequests(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl + 'admins/getAllRegistrationRequests');
  }

  updateUserData(userData: any): Observable<ResponseMessage> {
    return this.http.put<ResponseMessage>(this.apiUrl + 'admins/updateAdminData', userData);
  }

  processRegistrationRequest(responseData: RegistrationRequestResponse): Observable<User[]> {
    return this.http.put<User[]>(this.apiUrl + 'admins/processRegistrationRequest', responseData);
  }

  getAllRoles(): Observable<UserRole[]> {
    return this.http.get<UserRole[]>(this.apiUrl + 'admins/getAllRoles');
  }

  getAllPermissionsForRole(userRole: UserRole): Observable<Permission[]> {
    return this.http.get<Permission[]>(this.apiUrl + 'admins/getAllPermissionsForRole/' +  userRole);
  }

  removePermission(permission: Permission, role: UserRole): Observable<ResponseMessage> {
    const data = {
      permission: permission,
      role: role
    };
    return this.http.put<ResponseMessage>(this.apiUrl + 'admins/removePermission', data);
  }

  getAllCanBeAddedPermissions(userRole: UserRole): Observable<Permission[]> {
    return this.http.get<Permission[]>(this.apiUrl + 'admins/getAllCanBeAddedPermissions/' +  userRole);
  }

  addPermission(permission: Permission, role: UserRole): Observable<ResponseMessage> {
    const data = {
      permission: permission,
      role: role
    };
    return this.http.put<ResponseMessage>(this.apiUrl + 'admins/addPermission', data);
  }

  //EMPLOYEE

  //...

  registerUser(user: User): Observable<ResponseMessage> {
    return this.http.post<ResponseMessage>(this.apiUrl + 'users/registerUser', user);
  }

  changePassword(passwordData: any): Observable<ResponseMessage> {
    return this.http.put<ResponseMessage>(this.apiUrl + 'users/updatePassword', passwordData);
  }

  changeAdminPassword(passwordData: any): Observable<ResponseMessage> {
    return this.http.put<ResponseMessage>(this.apiUrl + 'users/updateAdminPassword', passwordData);
  }

  tryLogin(email: string, password: string): Observable<LoginReponse> { 
    const loginData = {
      email: email,
      password: password
    };
    return this.http.post<LoginReponse>(this.apiUrl + 'users/tryLogin', loginData);
  }
  
  findUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(this.apiUrl + 'users/findUserByEmail/' + email);
  }
  
  updateClient(user: User): Observable<any> {
    return this.http.put<any>(this.apiUrl + 'users/updateClient', user, { responseType: 'text' as 'json' });
  }

  createAdRequest(adRequest: AdRequest): Observable<string> {
    return this.http.post<string>(this.apiUrl + 'ad-requests/create', adRequest, { responseType: 'text' as 'json' });
  }

  getAllAdRequests(): Observable<AdRequest[]> {
    return this.http.get<AdRequest[]>(this.apiUrl + 'ad-requests/all');
  }

  createAd(ad: Ad): Observable<string> {
    return this.http.post<string>(this.apiUrl + 'ads/create', ad, { responseType: 'text' as 'json' });
  }

  getAdRequestById(id: number): Observable<AdRequest> {
    return this.http.get<AdRequest>(`${this.apiUrl}ad-requests/${id}`);
  }

  getAllAds(): Observable<Ad[]> {
    return this.http.get<Ad[]>(this.apiUrl + 'ads/all');
  }

  getAllAdsByEmail(email: string): Observable<Ad[]> {
    return this.http.get<Ad[]>(this.apiUrl + 'ads/by-email', { params: { email } });
  }

  getLoggedInUser(): Observable<User> {
    return this.http.get<User>(this.apiUrl + "users/getLoggedInUser");
  }


  getLoggedInUserHomepage(accessToken : string): Observable<User> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${accessToken}`
    });
    return this.http.get<User>(this.apiUrl + "users/getLoggedInUser", { headers});
  }

}
