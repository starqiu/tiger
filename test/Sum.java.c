// This is automatically generated by the Tiger compiler.
// Do NOT modify!

// structures
struct Sum
{
    struct Sum_vtable *vptr;
};

struct Doit
{
    struct Doit_vtable *vptr;
};

// vtables structures
struct Sum_vtable
{
};

struct Doit_vtable
{
    int(*doit)(struct Doit*, int);
};

// method declarations
int Doit_doit(struct Doit * this, int n);

// vtables
struct Sum_vtable Sum_vtable_ = 
{
};

struct Doit_vtable Doit_vtable_ = 
{
    Doit_doit,
};

// methods
int Doit_doit(struct Doit * this, int n)
{
    int sum;
    int i;
    i = 0;
    sum = 0;
    while(i < n)
        sum = sum + i;
    return sum;
}

// main method
int Tiger_main()
{
    struct Doit * x_0;
    System_out_println((x_0=((struct Doit*)(Tiger_new(&Doit_vtable_, sizeof(struct Doit)))), x_0->vptr->doit(x_0, 101)));
}

