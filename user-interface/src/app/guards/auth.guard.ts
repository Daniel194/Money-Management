import {Injectable} from '@angular/core';
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {AuthenticationService} from "../service/authentication.service";


@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {

    constructor(private router: Router) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const expectedRoles = route.data.expectedRoles;

        if (!AuthenticationService.isUserLogin()) {
            this.router.navigate(['']);
            return false;
        } else if (!AuthenticationService.isInRoles(expectedRoles)) {
            this.router.navigate(['/statistics']);
            return false;
        }

        return true;
    }
}
