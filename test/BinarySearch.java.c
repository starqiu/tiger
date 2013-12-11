// This is automatically generated by the Tiger compiler.
// Do NOT modify!

// structures
struct BinarySearch
{
    struct BinarySearch_vtable *vptr;
};

struct BS
{
    struct BS_vtable *vptr;
    int * number;
    int size;
};

// vtables structures
struct BinarySearch_vtable
{
};

struct BS_vtable
{
    int(*Start)(struct BS*, int);
    int(*Search)(struct BS*, int);
    int(*Div)(struct BS*, int);
    int(*Compare)(struct BS*, int, int);
    int(*Print)(struct BS*);
    int(*Init)(struct BS*, int);
};

// method declarations
int BS_Start(struct BS * this, int sz);
int BS_Search(struct BS * this, int num);
int BS_Div(struct BS * this, int num);
int BS_Compare(struct BS * this, int num1, int num2);
int BS_Print(struct BS * this);
int BS_Init(struct BS * this, int sz);

// vtables
struct BinarySearch_vtable BinarySearch_vtable_ = 
{
};

struct BS_vtable BS_vtable_ = 
{
    BS_Start,
    BS_Search,
    BS_Div,
    BS_Compare,
    BS_Print,
    BS_Init,
};

// methods
int BS_Start(struct BS * this, int sz)
{
    int aux01;
    int aux02;
    struct BS * x_1;
    struct BS * x_2;
    struct BS * x_3;
    struct BS * x_4;
    struct BS * x_5;
    struct BS * x_6;
    struct BS * x_7;
    struct BS * x_8;
    struct BS * x_9;
    struct BS * x_10;
    aux01 = (x_1=this, x_1->vptr->Init(x_1, sz));
    aux02 = (x_2=this, x_2->vptr->Print(x_2));
    if((x_3=this, x_3->vptr->Search(x_3, 8)))
        System_out_println(1);
    else
        System_out_println(0);
    if((x_4=this, x_4->vptr->Search(x_4, 19)))
        System_out_println(1);
    else
        System_out_println(0);
    if((x_5=this, x_5->vptr->Search(x_5, 20)))
        System_out_println(1);
    else
        System_out_println(0);
    if((x_6=this, x_6->vptr->Search(x_6, 21)))
        System_out_println(1);
    else
        System_out_println(0);
    if((x_7=this, x_7->vptr->Search(x_7, 37)))
        System_out_println(1);
    else
        System_out_println(0);
    if((x_8=this, x_8->vptr->Search(x_8, 38)))
        System_out_println(1);
    else
        System_out_println(0);
    if((x_9=this, x_9->vptr->Search(x_9, 39)))
        System_out_println(1);
    else
        System_out_println(0);
    if((x_10=this, x_10->vptr->Search(x_10, 50)))
        System_out_println(1);
    else
        System_out_println(0);
    return 999;
}

int BS_Search(struct BS * this, int num)
{
    int bs01;
    int right;
    int left;
    int var_cont;
    int medium;
    int aux01;
    int nt;
    struct BS * x_11;
    struct BS * x_12;
    struct BS * x_13;
    aux01 = 0;
    bs01 = 0;
    right = *(this->number - 1);
    right = right - 1;
    left = 0;
    var_cont = 1;
    while(var_cont)
    {
        medium = left + right;
        medium = (x_11=this, x_11->vptr->Div(x_11, medium));
        aux01 = this->number[medium];
        if(num < aux01)
            right = medium - 1;
        else
            left = medium + 1;
        if((x_12=this, x_12->vptr->Compare(x_12, aux01, num)))
            var_cont = 0;
        else
            var_cont = 1;
        if(right < left)
            var_cont = 0;
        else
            nt = 0;
    }
    if((x_13=this, x_13->vptr->Compare(x_13, aux01, num)))
        bs01 = 1;
    else
        bs01 = 0;
    return bs01;
}

int BS_Div(struct BS * this, int num)
{
    int count01;
    int count02;
    int aux03;
    count01 = 0;
    count02 = 0;
    aux03 = num - 1;
    while(count02 < aux03)
    {
        count01 = count01 + 1;
        count02 = count02 + 2;
    }
    return count01;
}

int BS_Compare(struct BS * this, int num1, int num2)
{
    int retval;
    int aux02;
    retval = 0;
    aux02 = num2 + 1;
    if(num1 < num2)
        retval = 0;
    else
        if(!(num1 < aux02))
            retval = 0;
        else
            retval = 1;
    return retval;
}

int BS_Print(struct BS * this)
{
    int j;
    j = 1;
    while(j < (this->size))
    {
        System_out_println(this->number[j]);
        j = j + 1;
    }
    System_out_println(99999);
    return 0;
}

int BS_Init(struct BS * this, int sz)
{
    int j;
    int k;
    int aux02;
    int aux01;
    this->size = sz;
    this->number = ((int *)(Tiger_new_array(sz * sizeof(int))));
    j = 1;
    k = this->size + 1;
    while(j < (this->size))
    {
        aux01 = 2 * j;
        aux02 = k - 3;
        this->number[j] = aux01 + aux02;
        j = j + 1;
        k = k - 1;
    }
    return 0;
}

// main method
int Tiger_main()
{
    struct BS * x_0;
    System_out_println((x_0=((struct BS*)(Tiger_new(&BS_vtable_, sizeof(struct BS)))), x_0->vptr->Start(x_0, 20)));
}

