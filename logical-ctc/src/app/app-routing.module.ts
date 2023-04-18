import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HomepageComponent } from './homepage/homepage.component';
import { AdminpageComponent } from './adminpage/adminpage.component';
import { AdduserComponent } from './adduser/adduser.component';
import { AddquestionsComponent } from './addquestions/addquestions.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { RightSlideComponent } from './right-slide/right-slide.component';

const routes: Routes = [
  {path: 'admin', component: AdminpageComponent},
  { path: "app-login", component: RightSlideComponent },
  { path: 'user', component: AdduserComponent },
  { path: '', component: DashboardComponent, pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent, }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
