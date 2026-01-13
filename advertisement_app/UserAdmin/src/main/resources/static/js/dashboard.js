document.addEventListener("DOMContentLoaded", () => {
  updatePageDropdown();
});

//HANDLER
const handleViewDetails = async (id) => {
  const userCard = document.getElementById(`user-card-${id}`);
  
  // Hide any currently visible user details
  document.querySelectorAll('.dashboard-user-card.visible').forEach(element => {
    element.classList.remove('visible');
  });

  // Show the selected card
  userCard?.classList.add('visible');

  // Fetch location data from Nominatim
  try {
    const latitude = userCard.dataset.lat;
    const longitude = userCard.dataset.long;
    
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json`
    );
    const locationData = await response.json();

    updateLocation(id, locationData.display_name);
  } catch (error) {
    console.error("Failed to fetch location:", error);
  }
};

const updateLocation = (id, displayName) => {
  if (!id) return;

  const locationElement = document.getElementById(`location-${id}`);
  if (!locationElement) return;

  locationElement.textContent = displayName || "No location specified";
};

const handleClose = (id) => {
  const userCard = document.getElementById(`user-card-${id}`);
  userCard?.classList.remove('visible');
};

const goToPage = (page) => {
  const validatedPage = validateAndConvertPage(page);
  if (validatedPage === null) return;

  const pageDropdown = document.getElementById("pageDropdown");
  const size = pageDropdown.value;
  
  sessionStorage.setItem("currentSize", size);
  sessionStorage.setItem("currentPage", validatedPage);

  window.location.href = `/admin/v0/users/dashboard?page=${validatedPage}&size=${size}`;
};

//END HANDLER


//UTIL
const validateAndConvertPage = (page) => {
  const currentSize = Number(sessionStorage.getItem("currentSize"));
  const totalRows = Number(sessionStorage.getItem("totalRows"));
  const totalPages = Math.ceil(totalRows / currentSize);
  const currentPage = Number(sessionStorage.getItem("currentPage"));

  // Handle number input
  if (typeof page === 'number') {
    return page >= 0 && page < totalPages ? page : null;
  }

  // Handle string input
  if (typeof page === 'string') {
    switch (page) {
      case 'first':
        return 0;
      case 'last':
        return totalPages - 1;
      case 'prev':
        return currentPage > 0 ? currentPage - 1 : null;
      case 'next':
        return currentPage < totalPages - 1 ? currentPage + 1 : null;
      default:
        return null;
    }
  }

  return null;
};
//END UTIL



//UPDATE VIEW
const updatePageDropdown = () => {
  // Initialize current page if not set
  let currentPage = sessionStorage.getItem("currentPage");
  if (currentPage === null) {
    currentPage = 0;
    sessionStorage.setItem("currentPage", currentPage);
  }

  // Store total pages
  const paginationElement = document.getElementById("page-container-right-pagination");
  const totalPages = paginationElement?.dataset.totalPages || 0;
  sessionStorage.setItem("totalPages", totalPages);

  // Store total rows
  const totalRowsElement = document.getElementById("total-rows");
  const totalRows = totalRowsElement?.dataset.totalRows || 0;
  sessionStorage.setItem("totalRows", totalRows);

  // Initialize page size if not set
  let pageSize = sessionStorage.getItem("currentSize");
  if (!pageSize) {
    pageSize = 6;
    sessionStorage.setItem("currentSize", pageSize);
  }

  // Update dropdown selection
  const pageDropdown = document.getElementById("pageDropdown");
  if (pageDropdown) {
    Array.from(pageDropdown.options).forEach(option => {
      option.selected = option.value === pageSize;
    });
  }

  updateActivePagingButton(Number(currentPage));
};

const updateActivePagingButton = (pageNumber) => {
  const pageButtons = document.getElementsByClassName("pagination-btn");
  
  // Remove active class from all buttons
  Array.from(pageButtons).forEach(button => {
    button.classList.remove("active");
  });

  // Add active class to current page button
  const currentPageButton = document.getElementById(`pagination-btn-${pageNumber}`);
  if (currentPageButton) {
    currentPageButton.classList.add("active");
  }
};

//END UPDATE VIEW

