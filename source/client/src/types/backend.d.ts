export interface IBackendRes<T> {
  status: number | string;
  message?: string;
  error?: string;
  data?: T;
}

export interface IModelPaginate<T> {
  meta: {
    current: number;
    pageSize: number;
    pages: number;
    total: number;
  };
  result: T[];
}

export interface IMeta {
  current: number;
  pageSize: number;
  pages: number;
  total: number;
}

export interface IChat {
  id?: string;
  content: string;
  user?: {
    id: string;
    name: string;
  };
  createdBy?: string;
  isDeleted?: boolean;
  deletedAt?: boolean | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface IAccount {
  access_token: string;
  user: {
    id?: int;
    email: string;
    name: string;
    studentId?: string;
    role: string;
  };
}

export interface IGetAccount extends Omit<IAccount, "access_token"> {}

export interface IUser {
  id?: int;
  name: string;
  email: string;
  studentId?: string;
  role?: string;
  createdAt?: string;
}


export interface IProblem {
  id: int;
  title: string;
  description: string;
  qcode: string;
  protocolType: string;
  type: string;
  ioType: string;
  solved?: boolean;
}

export interface IRenponseString {
  message: string;
}
export interface ISubmission{
  id?: int;
  user: IUser;

  problem: IProblem

  inputData: string;
  studentResult: string;
  expectedResult: string;
  correct: boolean;
  createdAt?: string;
  status: string;
}

export interface IUserRank{
  user: IUser;
  totalSubmissions: number;
  correctSubmissions: number;
}

export interface ISubmissionFile {
  id?: int;
  filePath: string;
  problem: IProblem;
  createdAt: string;
}
