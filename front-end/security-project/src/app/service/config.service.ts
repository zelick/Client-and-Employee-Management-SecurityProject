import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {  //sta ce mi ovo?

  private _api_url = 'https://localhost:443/api';
  private _auth_url = 'https://localhost:443/api/auth';
  private _user_url = this._api_url + '/user';

  private _login_url = this._auth_url + '/login'; //AUTH URL!

  get login_url(): string {
    return this._login_url;
  }

  private _users_url = this._user_url + '/all';

  get users_url(): string {
    return this._users_url;
  }


  private _signup_url = this._auth_url + '/signup';

  get signup_url(): string {
    return this._signup_url;
  }

}