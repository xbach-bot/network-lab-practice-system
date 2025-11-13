import { setRefreshTokenAction } from "@/lib/redux/slice/auth.slice";
import { makeStore } from "@/lib/redux/store";
import {
  IBackendRes,
  IAccount,
  IUser,
  IModelPaginate,
  IGetAccount,
  IChat,
  IProblem,
  ISubmission,
} from "@/types/backend";
import { message, notification } from "antd";
import Cookies from "js-cookie";

const BACKEND_URL = process.env.NEXT_PUBLIC_API_URL;
const NO_RETRY_HEADER = "No-Retry";

interface FetchOptions extends RequestInit {
  headers: {
    [key: string]: string;
  };
}

const fetchWithInterceptor = async (
  url: string,
  options: FetchOptions = {
    headers: {},
  }
) => {
  // Pre-request interceptor
  if (typeof window !== "undefined" && localStorage.getItem("access_token")) {
    options.headers = {
      ...options.headers,
      Authorization: "Bearer " + localStorage.getItem("access_token"),
    };
  }
  if (!options.headers?.Accept && options.headers?.["Content-Type"]) {
    options.headers = {
      ...options.headers,
      Accept: "application/json",
      "Content-Type": "application/json; charset=utf-8",
    };
  }

  // Ensure credentials are included in the request
  options.credentials = "include";

  let response = await fetch(url, options);

  // Post-response interceptor
  if (!response.ok) {
    if (
      response.status === 401 &&
      url !== "/auth/login" &&
      !options.headers[NO_RETRY_HEADER]
    ) {
      const access_token = await refreshToken();
      options.headers[NO_RETRY_HEADER] = "true";
      if (access_token) {
        options.headers["Authorization"] = `Bearer ${access_token}`;
        localStorage.setItem("access_token", access_token);
        Cookies.set("access_token", access_token);
        response = await fetch(url, options);
      }
    }

    if (
      response.status === 400 &&
      url === "/auth/refresh" &&
      location.pathname.startsWith("/admin")
    ) {
      const message =
        (await response.json())?.error ?? "Có lỗi xảy ra, vui lòng login.";
      //dispatch redux action
      makeStore().dispatch(setRefreshTokenAction({ status: true, message }));
    }
    if (!response.ok) {
      const res = await response.json();
      return res;
    }
  }

  return response.json();
};


export const fetchChats = async ({
  page,
  size = 50,
  lastPage,
}: {
  page?: number;
  size?: number;
  lastPage?: boolean;
}): Promise<IBackendRes<IModelPaginate<IChat>>> => {
  const res = await fetch(
    `${BACKEND_URL}/chats?size=${size}${page ? `&page=${page}` : ""}${
      lastPage ? `&lastPage=${lastPage}` : ""
    }`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return await res.json();
};

export const createChat = async (body: IChat): Promise<IBackendRes<IChat>> => {
  const res = await fetchWithInterceptor(`${BACKEND_URL}/chats`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  return res;
};

export const deleteChat = async (id: string): Promise<IBackendRes<any>> => {
  const res = await fetchWithInterceptor(`${BACKEND_URL}/chats/${id}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return res;
};

// Auth Apis

export const callLogin = async (
  email: string,
  password: string
): Promise<IBackendRes<IAccount> | undefined> => {
  const res = await fetch(`${BACKEND_URL}/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify({ email, password }),
  });

  if (!res.ok) {
    notification.error({
      message: "Có lỗi xảy ra",
      description: await res.json().then((data) => data.error),
    });
    return;
  }
  const data = await res.json();
  return data;
};

export const callRegister = async (
  body: IUser
): Promise<IBackendRes<IUser>> => {
  const res = await fetch(`${BACKEND_URL}/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },

    body: JSON.stringify(body),
  });
  const data = await res.json();
  return data;
};

export const callFetchAccount = async (
  accessToken = ""
): Promise<IBackendRes<IGetAccount> | undefined> => {
  const res = await fetchWithInterceptor(`${BACKEND_URL}/auth/account`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${
        localStorage.getItem("access_token") ?? accessToken
      }`,
    },
  });
  if (!res) {
    return Promise.resolve(undefined);
  }

  return res;
};

export const refreshToken = async (): Promise<string | null> => {
  const res = await fetch(`${BACKEND_URL}/auth/refresh`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
  });
  if (!res.ok) {
    return null;
  }
  const data: IBackendRes<IAccount> = await res.json();
  return data.data?.access_token || null;
};

export const logout = async (): Promise<void> => {
  const res = await fetchWithInterceptor(`${BACKEND_URL}/auth/logout`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${localStorage.getItem("access_token")}`,
    },
    credentials: "include",
  });
  if (res.status === 400) {
    notification.error({
      message: "Có lỗi xảy ra",
      description: res.error,
    });
    return;
  }

  return res;
};


// Problem Apis

export const fetchProblems = async ({
  page = 1,
  size = 10,
  name = "",
}: {
  page?: number;
  size?: number;
  name?: string;
}): Promise<IBackendRes<IModelPaginate<IProblem>> | undefined> => {
  try {
    let url = `${BACKEND_URL}/problems?size=${size}&page=${page}`;

    if (name && name.trim() !== "") {
      url += `&filter=title ~~ '*${name}*'`;
    }

    const res = await fetchWithInterceptor(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("access_token")}`,
      },
    });

    if (!res) {
      return;
    }

    return res;
  } catch (error: any) {
    
  }
};

export const fetchProblemByQCode = async (
  qCode: string
): Promise<IBackendRes<IProblem> | undefined> => {
  try {
    const res = await fetchWithInterceptor(
      `${BACKEND_URL}/problems/get-one/${qCode}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("access_token")}`,
        },
      }
    );

    if (!res) {
      return;
    }

    return res;
  } catch (error: any) {
    
  }
};


// Submission Apis

export const fetchSubmissions = async ({
  page = 1,
  size = 10,
  qCode = "",
}: {
  page?: number;
  size?: number;
  qCode?: string;
}): Promise<IBackendRes<IModelPaginate<ISubmission>> | undefined> => {
  try {
    let url = `${BACKEND_URL}/submissions?size=${size}&page=${page}`;

    if (qCode && qCode.trim() !== "") {
      url += `&filter=problem.qCode ~~ '*${qCode}*'`;
    }

    const res = await fetchWithInterceptor(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("access_token")}`,
      },
    });

    if (!res) {
      return;
    }

    return res;
  } catch (error: any) {
    
  }
};