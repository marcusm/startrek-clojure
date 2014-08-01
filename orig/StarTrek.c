
/* StartTrek.c   */
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <time.h>   /* Needed just for srand seed */

struct quadrant {
	int KlingonCount;
	int BaseHere;
	int StarCount;
};

struct klingon {
	int Energy;
	int X;
	int Y;
};

/* Enums for damage array, 0 not used */
const  int short_range_sensors=1;
const  int computer_display=2;
const  int long_range_sensors=3;
const  int phasers=4;
const  int warp_engine=5;
const  int photon_torpedo_tubes=6;
const  int shield=7;

int Sectors[9][9];
struct quadrant Quadrants[9][9];
struct klingon Klingons[ 5 ];
int Damage[8];  /*  Systems ok =0, >0= not working */

/*Game Status Variables */
int NumKlingons = 0;
int KlingonsHere =0;
int NumBases=0;
int Numdays=0;
int NumTorps=0;
int NumCasualties =0;
int NumDeadKlingons=0;

/* Enterprise Location, Energy etc*/
int EQuadrantX=0;
int EQuadrantY=0;
int ESectorX=0;
int ESectorY=0;
int IsDocked=0;
int Energy=0;

/* General Global Variables */
int Dir=0;
int Speed;
int X;
int Y;
int OffX;
int OffY;
int Range;
int CanExit=0;

/*function prototypes  */
 void InitVars();
 void DoReport();
 void ShowStatus( int Device);
 void HitKlingons();
 void ShowEnterpriseLoc();
 void HitKlingon();
 void GetCommand();
 void EnterQuadrant(int QuadrantX,int QuadrantY);

 /* Commands */
 void DoReport();			/* R */
 void DoLRScan();			/* L */
 void DoSRS();			/* S */
 void FirePhasers();		/* F */
 void FirePhotonTorpedo();	/* T */
 void ShowGalaxyMap();		/* G */
 void DoWarp();				/* W */
 void DoQuit();				/* Q */

  int InputDirection( );
 int  IsSectorValid(int x,int y);
 void TakeDamage( int Total); 
 void FindEmptySector( int * X,int * Y );
 int  InputDirection();
 void PlaceBases( int NumBases );
 void PlaceStars( int NumStars );
 void PlaceKlingons(int NumKlingons );
 void PopulateQuadrant(int X,int Y );
 void LeaveGalaxy(int Speed);
 void PhotonHitKlingon(int X,int Y);
 void PhotonHitBase(int X,int Y);
 void PhotonHitStar(int X,int Y);

 char* strupper(char *s);

 int Random(int Max) {
	  return ( rand() % Max)+ 1; 
   }

 int GetKlingons(int diff) {
	   /* was M=(M<209) || (M<99)||(M<49)||(M<24)||(M<9)||(M<2); */
	   int dice;
	   dice= Random( diff);
	   if  (dice < 2)
		   return 5;
	   
	   if  (dice < 9)
		   return 5;
	   if  (dice < 24)
		   return 4;
	   if  (dice < 49)
		   return 3;
	   if  (dice < 99)
		   return 2;
	   if  (dice < 209)
		   return 1;
	  return 0;
   }

  void SetupGalaxy(int Difficulty) {


  int x,y,J,temp,BaseHere;
  do { 
	  /* Clear all data */
	  for (x=1;x <=8; x++) 	  { 
		  for (y=1;y<=8;y++)    {
			  Sectors[x][y]=0;
			  Quadrants[x][y].BaseHere=0;
			  Quadrants[x][y].KlingonCount=0;
			  Quadrants[x][y].StarCount=0;
		  }
	  }


	  KlingonsHere=0; /* 15 */
	  NumBases=0;

	  for (x=1; x<=8;x++) {
		  for (y=1;y<=8;y++)  {
		 J= Random(99);
		 BaseHere=0;
		 if (J<5) 
		 {
			 NumBases++;
			 BaseHere=1;
		 }
		 temp=GetKlingons(Difficulty);
		 
		 KlingonsHere += temp;
		 Quadrants[x][y].BaseHere=BaseHere;
		 Quadrants[x][y].KlingonCount = temp;
		 Quadrants[x][y].StarCount =Random(3);
	  }
	  }
  }
  while (NumBases<2 || KlingonsHere<4);
  NumKlingons = KlingonsHere;	 
 }
/* ----------------------- */

  int IsSectorValid(int x,int y) {
	  return (x >=1 && x < 9 && y>=1 && y < 9);
  }

	 /* check for presence of Starbase */
	void CheckStarbase() {  /* 145 */
		int I,J,X,Y;
	IsDocked =0;
    for (I=-1;I<=1;I++)
	{
       X= I+EQuadrantX;
	   for (J=-1; J< 1; J++) 
	   {
		   Y = J + EQuadrantY;
		   if (IsSectorValid(X,Y) && Quadrants[X][Y].BaseHere)
		   IsDocked=1;
	   }
	}
	 return;
	}

	/* CheckIfDocked Function */
	void CheckIfDocked() { /* 155 */
	
    if (IsDocked) 
		printf("SULU: CAPTAIN, WE ARE DOCKED AT STARBASE.");
    InitVars();
    return;
	}	

	/* InitVars */
	 void InitVars() { /* 160 */
 	int I;
     Energy=4000;
	 Numdays=30;
     NumTorps=10,
	 IsDocked=0;
	 for (I = 1; I<= 7;I++)  /* All systems ok */
		 Damage[I]=0;
	 NumDeadKlingons=0;
	 NumCasualties=0;
     return;
	}

	/* Get player input and process it */
	void GetCommand() {/*  120   */
	char  Command[5];
	do {
  		printf("Command:");
		gets(Command);
		strcpy(Command,strupper(Command));
		 
		switch ( Command[0] ) {
		case 'Q' : DoQuit(); return;
			case 'R' : DoReport();continue;
			case 'S' : DoSRS();continue;
			case 'L' : DoLRScan();continue;
			case 'P' : FirePhasers();return;
			case 'G' : ShowGalaxyMap();continue;
			case 'T' : FirePhotonTorpedo();return;
			case 'W' : DoWarp();return;
		
		 default : {
					printf("R=REPORT       S=SR SENSOR   L=LR SENSOR \n\r");
					printf("G=GALAXY MAP   P=PHASER       T=TORPEDO \n\r");
					printf("W=WARP ENGINE  **PLEASE USE ONE OF THESE COMMANDS***\n\r");
					}
			} 
		}
	   while (1);

	   return;
	 }
 /*165 S=Random(8),T=Random(8),A=8*S+T+62;if (data[A) goto 165 */
 void FindEmptySector(int * X,int * Y ) {
	 do {
	   *X = Random(8)+1;
	   *Y = Random(8)+1;
	 }
	 while (Sectors[*X][*Y]);
 	return;
	 }


 void ShowEnterpriseLoc() {  /* 175 */
   printf("ENTERPRISE IN QUADRANT - (%i %i) SECTOR (%i %i)\n\r",EQuadrantX,EQuadrantY,ESectorX,ESectorY);
   return ;
 }


void ShowGalaxyMap() { /* 180 */
	int x,y,Device,aquadrant;
	ShowEnterpriseLoc();
	Device=computer_display;
	ShowStatus( Device); /* 375;  */
	if (Damage[ Device ])
		return;
	printf("GALAXY MAP\n\r    ");
	for (x=1;x<=8;x++)
		printf("%3i ",x);
	printf("\n\r   ---------------------------------\n\r");
	for (y=1; y <= 8;y++) {
		printf("%i | ",y);       
		for (x=1; x<=8 ;x++) {
		aquadrant = Quadrants[x][y].KlingonCount*100 +
			        Quadrants[x][y].BaseHere*10 +
					Quadrants[x][y].StarCount;
			printf("%3i ",aquadrant);
		}
		printf(" | %i\n\r",y);
	}
	printf("   ---------------------------------\n\r    ");
	for (x=1;x<=8;x++)
		printf("%3i ",x);

		printf("\n\r"); 
		return;

}

 /*  short_range_sensor=1;
  =3;
  phaser=4;
  warp_engine=5;
  photo_torpedo_tubes=6;
  shield=7;
  */

/* ------------------ */
 void DoLRScan() {/* 200  */
	int x,y,xl,yl,lrvalue,Device;
	ShowEnterpriseLoc();
	Device=long_range_sensors;
	ShowStatus(Device); /* 375  */
	if (Damage[ Device])
		return;
	printf("\n\r");
	for (y=-1;y<=1;y++) {
		yl = y + EQuadrantY;
		for (x=-1;x <=1;x++) {
			xl = x + EQuadrantX;
	
            lrvalue =0;
            if ( IsSectorValid(xl,yl) )			{
				lrvalue =	Quadrants[xl][yl].KlingonCount*100 + 
							Quadrants[xl][yl].BaseHere*10 +
							Quadrants[xl][yl].StarCount;
			}

		printf("%3i ",lrvalue);
		}
	 printf("\n\r");
	}
	printf("\n\r");
	return;
	}
/* -------------------  */

	void DoSRS() {/* 220  */
		
	int x,y,Device,S;

	ShowEnterpriseLoc();
	Device=short_range_sensors;

	ShowStatus( Device); /* 375;if (I/* Goto 120  */
	if (Damage[ Device])
		return;
	printf("\n\r    ");
	for (x=1; x<=8;x++)
	   printf("%i ",x);
	printf("\n\r   ----------------\n\r");
	for (y=1;y<=8;y++) {
		printf("%i |",y);
		for (x=1;x<=8;x++) {
			S= Sectors[x][y];
			switch (S) {
			case 0 :printf(" .");break;
			case 1 : printf(" K");break;	
			case 2 : printf(" B");break; 
			case 3 : printf(" *");break; 	
			case 4 : printf(" E");break; 
			default : printf("%x",S);break;
			}    
		}
		printf("| %i\n\r",y);
		}
	printf("   ----------------\n\r");
	printf("    ");
	for (x=1; x<=8;x++)
	   printf("%i ",x);
	printf("\n\r"); 
    return;
	}

/*------------------- */
	void FirePhasers() { /* 260   */
		char Blast[10];
		int BlastEnergy;
		int Klindex;
		int x,y,Device;
		int power=0;
     Device=phasers;
	 ShowStatus( Device); /* 375; */
	 if (Damage[ Device ])
		 return;

    printf(" ENERGIZED. UNITS TO FIRE? :");
	gets( Blast);
	BlastEnergy = atoi(Blast);
	if (BlastEnergy <1)
	   return;
	if (BlastEnergy > Energy) 
	{
		 printf("SPOCK: WE HAVE ONLY %i ENERGY UNITS. \n\r",Energy);
		return;
	}
    Energy-=BlastEnergy;
	if (KlingonsHere <1 ) {
		printf("PHASER FIRED AT EMPTY SPACE. \n\r"); 
		return;
	}
	BlastEnergy /= KlingonsHere;
    for ( Klindex=0;Klindex <5;Klindex++) 
		if (Klingons[Klindex].Energy> 0) {     
			if ( BlastEnergy>1090)   {
				printf("...OVERLOADED..");
				Damage[ phasers] =1;
				BlastEnergy=9;
				ShowStatus( phasers ); 
			}	
		  		
		  x=(Klingons[Klindex].X-ESectorX);
	      y=(Klingons[Klindex].Y-ESectorY);
	  	  power=BlastEnergy*30/(30+(x*x)+(y*y)+1);
		  printf("%i UNITS HIT \n\r",power);
		  HitKlingon(Klindex,power);
		}
	  
  return;
	}	 

/* ------------------------------------  */

	 void HitKlingon(int Klindex,int BlastPower) {
		int Kx = Klingons[Klindex].X;
		int Ky = Klingons[Klindex].Y;

	 if (BlastPower==0)
		 return;
	 printf("KLINGON AT S-(%i %i) ",Kx,Ky);
	 Klingons[Klindex].Energy -= BlastPower; 
     if ( Klingons[Klindex].Energy >0 )
		{
			 printf("  **DAMAGED**\n\r");
			 return;
		}
	 /* Destroyed if here */
     Klingons[Klindex].Energy=0;
	 KlingonsHere = --(Quadrants[EQuadrantX][EQuadrantY].KlingonCount);
	 
	 Sectors[Kx][Ky]=0;
    
	 NumKlingons=NumKlingons-1;
     
     printf("  ***DESTROYED***\n\r");
     return ;
	 }
	 
	 /* ---------------------------------- */
	 void DoKlingonAttack( int Attack) {/* 325 */
		 int Total=0;
		 int Klindex,Kx,Ky;

     if (Quadrants[EQuadrantX][EQuadrantY].KlingonCount==0)
       return;
	 printf("KLINGON ATTACK\n\r");
	 if (IsDocked) {
	     printf("STARBASE PROTECTS ENTERPRISE\n\r");
	     return;
	 }
	 
	 for (Klindex =0; Klindex < 5;Klindex++) {   /* 335 */
	   if (Klingons[Klindex].Energy>0) 
		{	
			Attack = Klingons[Klindex].Energy;
			Attack = Random( Attack);
			Total += Attack;
			Kx = Klingons[Klindex].X;
			Ky = Klingons[Klindex].Y;
            printf("%i UNITS HIT FROM KLINGON AT S-(%i %i) \n\r",Attack,Kx,Ky);
		}
	 }
	 Energy-=Total;
	 if (Energy<=0)
	 {
		 printf("*** BANG ***\n\r");
		 return;
	 }
	 printf("%i UNITS OF ENERGY LEFT.\n\r",Energy);
	 if (Random(Energy/4)>Total)  /* was it a piffling little attack? */
		 return;  /* yes   */

	 /* Oh oh Shields Damaged     */
	 if ( Damage[ shield] ==0)
		{
		 Damage[shield]=Random(Total/50+1),
		 ShowStatus( shield);
		 return;
		}
	 /* And random damage ...*/
     TakeDamage( Total);
	 return;
	}

 void TakeDamage( int Total) {
 int Casualties;
 int Device=Random(6);
 Damage[ Device] += Random(Total/99+1);

 /* there go the red shirts...*/
 Casualties=Random(8)+1,
 NumCasualties += Casualties;
 printf("MC COY: SICKBAY TO BRIDGE, WE SUFFERED %i CASUALTIES.\n\r",Casualties);
 ShowStatus( Device);
 return;
 }

 void ShowStatus(int Device) { /* 375   */
   
   int I=Damage[Device];
   switch ( Device)  {
        case 1 : printf("SHORT RANGE SENSOR "); break;
		case 2 : printf("COMPUTER DISPLAY ");break;
		case 3 :printf("LONG RANGE SENSOR ");break;
		case 4 :printf("PHASER "); break;
		case 5 :printf("WARP ENGINE ");break;
		case 6 :printf("PHOTON TORPEDO TUBES ");break;
		case 7 :printf("SHIELD ");break;
   }
   if (I==0) 
      return;
   printf("DAMAGED, %i STARDATES ESTIMATED FOR REPAIR\n\r",I);
   return; 
 }
 
 void DoReport() {
	int Device;
   printf("STATUS REPORT:\n\r");
   printf("==============\n\r");
   printf("STARDATE %i",3230-Numdays);
   printf("  TIME LEFT %i\n\r",Numdays );
   printf("CONDITION : ");
   if (IsDocked) 
	   printf("DOCKED");
   else
   {
	if (NumKlingons) 
	   printf("RED");/* Goto 445   */
	else
	  if (Energy>=999) 
         printf("YELLOW");
	   else
         printf("GREEN"); 
   }
  printf("\n\rPOSITION      Q-(%i %i) S-(%i %i)\n\r",EQuadrantX,EQuadrantY,ESectorX,ESectorY);
  printf("ENERGY :%i\n\r",Energy);
  printf("TORPEDOES :%i\n\r",NumTorps);
  printf("KLINGONS LEFT :%i\n\r",NumKlingons);
  printf("STARBASES : %i\n\r",NumBases);
  for (Device=1;Device<=7;Device++)
     if (Damage[Device])
		ShowStatus( Device );
  
     return;
 }
	
 void EnterQuadrant(int QuadrantX,int QuadrantY)
 {
	 EQuadrantY=QuadrantY;
	 EQuadrantX=QuadrantX;
	 PopulateQuadrant(QuadrantX,QuadrantY);
	 FindEmptySector( &ESectorX,&ESectorY );
	 Sectors[ESectorX][ESectorY]=4;
	 DoSRS();
 }

/*------------------------------------  */
	void DoWarp() {/* 465  */
	int DamageIndex;
	int index,I,J,Xs,Ys, Device; 
	char StrSpeed[10];
	Device=warp_engine;
	ShowStatus( Device); /* 375;     */
	if (Damage[Device]>3)
		return;
	do {
		printf("SECTOR DISTANCE TO TRAVEL:");
		gets(StrSpeed);
		Speed = atoi( StrSpeed);
        if (Speed<1)
          return;

        if (Damage[Device] > 0 && (Speed>2))
			{
				printf("CHEKOV: WE CAN TRY 2 AT MOST, SIR ");
				continue;
			}
		break;
		}
		while (1);
		   
	 if (Speed>91) 
		{
		   Speed=91;
		   printf("SPOCK: ARE YOU SURE, CAPTAIN?\n\r" );
		}

	 if (Energy<Speed*Speed/2) {
		   printf("SCOTTY: SIR, WE DO NOT HAVE THE ENERGY.\n\r");
		   return; 
		}

     Dir= InputDirection();
	 if (Dir==0)
		return; 
     Numdays--;
	 Energy-=(Speed*Speed)/2;
	 
	 Sectors[ESectorX][ESectorY]=0; 
	 /* Do Repairs   */
     for (DamageIndex=1;DamageIndex<=7;DamageIndex++) {
		  if ( Damage[ DamageIndex] > 0 )
		    Damage[DamageIndex]--;
     }
     Xs=45*ESectorX+22;
	 Ys=45*ESectorY+22,
	 Speed=45*Speed;
	 for (index=1;index<=8;index++) {
	    Speed=Speed-Range;
	if (Speed>=-22)
		{
		Xs+= OffX;
		Ys+= OffY;
		I=Xs/45;
		J=Ys/45;
		if  ( IsSectorValid( I,J) )
			{
			   if (EQuadrantX != I || EQuadrantY != J)
				   EnterQuadrant(I,J);
			}
		else
			LeaveGalaxy( Speed );
		}
	else
		{
			printf("**EMERGENCY STOP**");
			printf("SPOCK: TO ERR IS HUMAN\n\r " );
		}

 Sectors[ESectorX][ESectorY]=4;
 return;
	 }
}

	void LeaveGalaxy(int Speed)  {
	EQuadrantX= (EQuadrantX*72)+(ESectorX/5+Speed)/(5*Speed/Range)-9; /* 530  */
	ESectorX=EQuadrantX/72;
	EQuadrantY=(EQuadrantY*72)+(ESectorY/5+Speed)/(5*Speed/Range)-9;
	/*V=G/72;   */
	if (Random(9)<2) { /* 535  */
		printf("***SPACE STORM***");
        DoKlingonAttack( 100 );
		  
		if (  IsSectorValid( ESectorX,ESectorY)) {
			EQuadrantX=(EQuadrantX+9-72*ESectorX)/9,
			EQuadrantY=(EQuadrantY+9-72*ESectorY)/9;/* Goto 45 */
			return;
		}	
	}
	
 printf("**YOU WANDERED OUTSIDE THE GALAXY**\n\r"); 
 printf("ON BOARD COMPUTER TAKES OVER, AND SAVED YOUR LIFE\n\r");
     return;
  }

 void FirePhotonTorpedo() {/* 555  */
	 int Torpdir,Index,Xt,Yt,InSector,Xs,Ys,Device;
	 Device=photon_torpedo_tubes;
	 ShowStatus( Device); /* 375  */
	 if ( Damage[ Device ])
		 return;
	 if ( NumTorps==0)
	 {
		  printf(" NO TORPEDOES!\n\r");
		  return;
	 }
     printf("LOADED\n\r");
     Torpdir=InputDirection();
	 if (Torpdir==0)
		 return; 
     printf("TORPEDO TRACK\r\n ");
     NumTorps--;
/*	  570  print "TORPEDO TRACK ",;F=F-1,P=45*X+22,G=45*Y+22;F.M=1TO8 
      575 P=P+S,G=G+T,I=P/45,J=G/45;IF(I<1)+(I>8)+(J<1)+(J>8)G.585   */
	 Xt=45*ESectorX+22;
	 Yt=45*ESectorY+22;
	 for (Index=1;Index<=8;Index++) 
		{
			Xt+=OffX,
			Yt+=OffY,
			Xs=Xt/45,
			Ys=Yt/45;
			if (IsSectorValid(Xs,Ys)) { 
				InSector=Sectors[Xs][Ys]; 
				printf("%i %i",Xs,Ys);
				switch (InSector) {
					case 1 : PhotonHitKlingon(Xs,Ys); break;
					case 2 : PhotonHitBase(Xs,Ys); break;
					case 3 : PhotonHitStar(Xs,Ys); break;
					case 0 : continue;
				}
			}
	   }
	 
	printf("...MISSED");
	return;
 }

 /* torp hit Klingon. Which one?  */
 void PhotonHitKlingon(int X,int Y) { /* 590  */
	int Klindex;
	int  Blastpower =Random(99)+280;
	for (Klindex=1;Klindex<=5;Klindex++)
	 	if (Klingons[Klindex].X==X && Klingons[Klindex].Y==Y)
		  HitKlingon( Klindex, Blastpower );
	return;
 /*592 next M;/* Goto 65  */
 }

 void PhotonHitBase(int X,int Y) { /* 590  */
    NumBases--;  /* Doh!  */
    Quadrants[ EQuadrantX][EQuadrantY].BaseHere=0;
	Sectors[ESectorX][ESectorY]=0; 
	printf("STARBASE DESTROYED. ");
    printf("SPOCK: I OFTEN FIND HUMAN BEHAVIOUR FASCINATING.\n\r");
	return; 
 }

 void PhotonHitStar(int X,int Y) {
    printf("HIT A STAR");
	if (Random(9)<3 ) {
		printf("TORPEDO ABSORBED");
		return;
	}

 	
	if (Random(9)<6) {
		printf("STAR DESTROYED");
		Quadrants[X][Y].StarCount--;  /* 605    */
		printf("IT NOVAS    ***RADIATION ALARM***\n\r");
		TakeDamage( 300) ;
	}
    return; 
 }

 int InputDirection( ) {  /* 615  */
	int Dir;
	char angle[5];
	int Range;
	do {
		printf("COURSE (0-360):",&Dir);
		gets( angle );
	}
	while (strlen(angle)==0);
	Dir = atoi( angle );
	if (Dir>360 || Dir<0)
	return 0;

	OffX=(Dir+45)/90,
	Dir=Dir-OffX*90,
	Range=(45+Dir*Dir)/110+45;
	switch (Range ) {
		case 0 :	OffX=-45;   OffY= Dir;  return 1;break;
		case 1 :	OffX=Dir;   OffY=45;    return 1;break;
		case 2 :	OffX=45;    OffY=-Dir; return 1;break;
		case 3 :	OffX=-Dir;	OffY=-45;	return 1;break;
		default : return 1;
	}
 }

 void PlaceKlingons( int SomeKlingons ) {
    int Index;
	if (SomeKlingons==0)
		return;
	for (Index =1; Index <= SomeKlingons;Index++) {
        FindEmptySector( &X,&Y );

		Klingons[Index].Energy=300,
		Klingons[Index].X=X;
		Klingons[Index].Y=Y;
		Sectors[X][Y]=1; 
		}
	}

 void PlaceBases( int NumBases ) {
		int Index;
		int X,Y;
		if (NumBases==0)
			return;
		for (Index =1; Index <= NumBases; Index++) {
			FindEmptySector( &X,&Y );
			Sectors[X][Y]=2; 
		}
 }

 void PlaceStars( int NumStars ) {
		int Index;
		int X,Y;
		if (NumStars==0)
			return;
		for (Index =1; Index <= NumBases; Index++) {
			FindEmptySector( &X,&Y );
		Sectors[X][Y]=3; 
		}
 }

 void PopulateQuadrant(int X,int Y ) {
	 int Bases= Quadrants[X][Y].BaseHere;
	 int KlingonCount   = Quadrants[X][Y].KlingonCount;
	 int NumStars		= Quadrants[X][Y].StarCount;
     int Xs,Ys; 
    
    for (Xs=1;Xs<=8;Xs++)
		for (Ys=1;Ys<=8;Ys++)
			Sectors[Xs][Ys]=0;
    PlaceBases( Bases );
    PlaceKlingons( KlingonCount );
	PlaceStars( NumStars );
 }

 void Randomize() {
	srand( (unsigned)time( NULL ) );
 }

 // Set Flag so can exit
 void DoQuit()
 {
	 CanExit=1;
 }

char *strupper(char *s) { 
    unsigned c; 
    unsigned char *p = (unsigned char *)s; 
    while (*p)
    {
        c = *p;
        *p++ = toupper(c); 
    }

    return s;
} 


/* ------------------Main Program ------------------------------ */
int main(int argc, char* argv[])  {
	int Difficulty;
	int Score;
	int GameOver=0;
	char YesNo[5] ;

	Randomize();
	Difficulty=2999;
	printf("DO YOU WANT A DIFFICULT GAME? (Y OR N)");
	gets(YesNo); /* 5    */
	strcpy(YesNo,strupper(YesNo));

	printf("STARDATE 3200:");
	if (strcmp(YesNo,"Y") ==0)
	  Difficulty=999;  	 		

	do { /* Loop to control one complete game */
		/* Setup Everything  First Empty map */
		GameOver=0;
		SetupGalaxy( Difficulty );
		InitVars();

		/* Place Enterprise  */
		  ESectorX   = Random(8);
		  ESectorY   = Random(8);
		  EQuadrantX = Random(8);
		  EQuadrantY = Random(8);
		  PopulateQuadrant( EQuadrantX,EQuadrantY );
		  Sectors[ESectorX][ESectorY]=4;

		  printf("\n\rYOUR MISSION: TO DESTROY %i KLINGONS IN 30 STARDATES.",NumKlingons,"\n\r"); 
		  printf(" THERE ARE %i STARBASES.\n\r",NumBases);
		  
		  /* Main Game loop */
		  do {

			ShowEnterpriseLoc();
		//	ShowSRS();
			GetCommand();
			if (CanExit)
				break;
			CheckStarbase();
			DoKlingonAttack(0);

			if (NumKlingons==0) { /* we did it...killed em */
				printf("MISSION ACCOMPLISHED. ");
				if (Numdays<3)
					printf("BOY, YOU BARELY MADE IT");
				if (Numdays>5)
					printf("GOOD WORK...");
				if (Numdays>9) 
					printf("FANTASTIC!");
				if (Numdays>13)
					printf("UNBELIEVABLE!"); 
				Numdays=30-Numdays;
				Score=NumDeadKlingons*100/Numdays*10;
				printf("\n\r%i KLINGONS IN %i STARDATES. \n\r",NumDeadKlingons,Numdays);
				
				Score=-5*NumCasualties;
				if (NumCasualties==0)
					Score +=100;

				printf("%i CASUALTIES INCURRED. (",NumCasualties);
				printf(" )YOUR SCORE: %i\n\r",Score);
			}
			else {/* oh dear   */
				if (Numdays<0) 
					printf("IT'S TOO LATE, THE FEDERATION HAS BEEN CONQUERED.\n\r"); 
	  			if (Energy<=0) { /* 100   */
					printf("ENTERPRISE DESTROYED");
					if (NumDeadKlingons-NumKlingons>9) 
						printf(", BUT YOU WERE A GOOD MAN ");		  
					GameOver=1;
				}
				}
		  }
		  while (!GameOver);
		  /* At this point Game has finished. Start another? */
		Difficulty=987;
		printf("\n\r");
		printf("ANOTHER GAME? (Y OR N)");
		gets(YesNo);
		strcpy(YesNo,strupper(YesNo));
	}
	while ( strcmp(YesNo,"Y") ==0); 
	printf("GOOD BYE.\n\r\n\r");
    return 0;
  }
